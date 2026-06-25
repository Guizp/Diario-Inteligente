package com.example.diario_inteligente.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.databinding.ActivityAddReminderBinding
import com.example.diario_inteligente.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReminderBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("STUDENT_LOG", "Tela de adicionar lembrete aberta.")

        binding.btnSalvarLembrete.setOnClickListener {
            val titulo = binding.edtTituloLembrete.text.toString().trim()
            val descricao = binding.edtDescricaoLembrete.text.toString().trim()

            if (titulo.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                Log.w("STUDENT_LOG", "Tentou salvar com campos vazios.")
                return@setOnClickListener
            }

            salvarNoFirestore(titulo, descricao)
        }
    }

    private fun salvarNoFirestore(titulo: String, descricao: String) {
        val userIdAtual = auth.currentUser?.uid ?: ""

        val sdfData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dataAtual = sdfData.format(Date())
        val horaAtual = sdfHora.format(Date())

        val novoLembrete = Reminder(
            id = "",
            userId = userIdAtual,
            title = titulo,
            description = descricao,
            imageBase64 = "", // TODO: Sprint 3 - Câmera vai injetar a foto aqui
            date = dataAtual,
            time = horaAtual
        )

        Log.d("STUDENT_LOG", "Enviando dados para a coleção 'reminders'...")

        db.collection("reminders")
            .add(novoLembrete)
            .addOnSuccessListener { documentReference ->
                val idGerado = documentReference.id
                db.collection("reminders").document(idGerado).update("id", idGerado)

                Log.d("STUDENT_LOG", "Lembrete gravado com sucesso. ID: $idGerado")
                Toast.makeText(this, "Lembrete salvo com sucesso! 🚀", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("STUDENT_LOG", "Erro catastrófico ao salvar no banco", e)
                Toast.makeText(this, "Erro ao conectar com o banco de dados.", Toast.LENGTH_LONG).show()
            }
    }
}