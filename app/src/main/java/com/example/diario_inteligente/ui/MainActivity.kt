package com.example.diario_inteligente.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diario_inteligente.databinding.ActivityMainBinding
import com.example.diario_inteligente.helper.NotificationHelper
import com.example.diario_inteligente.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var lembreteAdapter: LembreteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MAIN_HOME", "MainActivity (Home) criada com sucesso.")

        configurarRecyclerView()

        binding.fabNovoLembrete.setOnClickListener {
            Log.d("MAIN_HOME", "Clicou no FAB. Indo para NovoLembreteActivity")
            val intent = Intent(this, AddReminderActivity::class.java)
            startActivity(intent)
        }

        binding.btnSair.setOnClickListener {
            Log.d("MAIN_HOME", "Usuário clicou em Sair. Deslogando...")
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.fabConfiguracoes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }

    override fun onStart() {
        super.onStart()
        carregarLembretesDoFirestore()
    }

    private fun configurarRecyclerView() {
        lembreteAdapter = LembreteAdapter(emptyList()) { lembreteClicado ->
            val intent = Intent(this, ReminderDetailActivity::class.java)
            intent.putExtra("LEMBRETE_ID", lembreteClicado.id)
            startActivity(intent)
        }

        binding.rvLembretes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = lembreteAdapter
        }
    }

    private fun carregarLembretesDoFirestore() {
        val userIdAtual = auth.currentUser?.uid ?: ""

        if (userIdAtual.isEmpty()) {
            Log.w("MAIN_HOME_WARN", "Usuário não está logado no Firebase. Cancelando busca.")
            return
        }

        Log.d("MAIN_HOME", "Buscando lembretes no Firestore do usuário: $userIdAtual")

        // Inicializa o seu helper de notificação
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Como você precisa passar o nome do usuário para a frase do Giovane,
        // pegamos o e-mail ou o nome do display (ou uma String padrão caso esteja vazio)
        val nomeUsuario = auth.currentUser?.displayName ?: "Usuário"

        db.collection("reminders")
            .whereEqualTo("userId", userIdAtual)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaLembretes = mutableListOf<Reminder>()

                for (documento in querySnapshot.documents) {
                    val lembrete = documento.toObject(Reminder::class.java)
                    if (lembrete != null) {
                        listaLembretes.add(lembrete)

                        // MÁGICA AQUI: Agenda o alarme de cada lembrete vindo do banco
                        notificationHelper.agendarNotificacao(lembrete, nomeUsuario)
                    }
                }

                Log.d("MAIN_HOME", "Total de lembretes encontrados e agendados: ${listaLembretes.size}")
                lembreteAdapter.atualizarLista(listaLembretes)
            }
            .addOnFailureListener { e ->
                Log.e("MAIN_HOME_ERR", "Erro ao buscar lembretes no Firestore", e)
                Toast.makeText(this, "Erro ao carregar seus lembretes.", Toast.LENGTH_SHORT).show()
            }
    }
}