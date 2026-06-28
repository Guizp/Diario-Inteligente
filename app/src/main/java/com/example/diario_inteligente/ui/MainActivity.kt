package com.example.diario_inteligente.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diario_inteligente.databinding.ActivityMainBinding
import com.example.diario_inteligente.helper.NotificationHelper
import com.example.diario_inteligente.model.Reminder
import com.example.diario_inteligente.sensor.LightSensorManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var lembreteAdapter: LembreteAdapter

    private lateinit var lightSensorManager: LightSensorManager
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MAIN_HOME", "MainActivity (Home) criada com sucesso.")

        lightSensorManager = LightSensorManager(this)
        preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)

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

    override fun onResume() {
        super.onResume()

        val usarTemaAutomatico = preferences.getBoolean("tema_automatico", true)

        if (usarTemaAutomatico) {
            lightSensorManager.startListening { escuro ->
                val modoAlvo = if (escuro) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }

                // so altera o tema e recria a tela se o modo atual for diferente do alvo
                if (AppCompatDelegate.getDefaultNightMode() != modoAlvo) {
                    AppCompatDelegate.setDefaultNightMode(modoAlvo)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        lightSensorManager.stopListening()
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

        Log.d("MAIN_HOME", "Buscando dados do usuário no Firestore: $userIdAtual")

        // buscamos primeiro o nome na colecao "users"
        db.collection("users").document(userIdAtual).get()
            .addOnSuccessListener { documentoUsuario ->

                val nomeUsuario = documentoUsuario.getString("name") ?: "Usuário"
                Log.d("MAIN_HOME", "Nome real encontrado: $nomeUsuario")

                val notificationHelper = NotificationHelper(this)
                notificationHelper.createNotificationChannel()


                db.collection("reminders")
                    .whereEqualTo("userId", userIdAtual)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val listaLembretes = mutableListOf<Reminder>()

                        for (documento in querySnapshot.documents) {
                            val lembrete = documento.toObject(Reminder::class.java)
                            if (lembrete != null) {
                                listaLembretes.add(lembrete)

                                // Agora enviamos o nome REAL recuperado do Firestore
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
            .addOnFailureListener { e ->
                Log.e("MAIN_HOME_ERR", "Erro ao buscar nome do usuário no Firestore", e)
                // se falhar o nome, não trava o app: carrega os lembretes com o valor padrão
                carregarLembretesSemNome()
            }
    }

    // metodo de segurança caso o Firestore falhe ao buscar o usuário
    private fun carregarLembretesSemNome() {
        val userIdAtual = auth.currentUser?.uid ?: ""
        val notificationHelper = NotificationHelper(this)

        db.collection("reminders")
            .whereEqualTo("userId", userIdAtual)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaLembretes = mutableListOf<Reminder>()
                for (documento in querySnapshot.documents) {
                    val lembrete = documento.toObject(Reminder::class.java)
                    if (lembrete != null) {
                        listaLembretes.add(lembrete)
                        notificationHelper.agendarNotificacao(lembrete, "Usuário")
                    }
                }
                lembreteAdapter.atualizarLista(listaLembretes)
            }
    }
}