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
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts
import com.example.diario_inteligente.converter.Base64Converter

class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReminderBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var imageBase64 = ""

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->

            bitmap?.let {

                binding.imgFotoLembrete.setImageBitmap(it)

                imageBase64 =
                    Base64Converter.bitmapToString(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("STUDENT_LOG", "Tela de adicionar lembrete aberta.")

        binding.btnTirarFoto.setOnClickListener {
            cameraLauncher.launch(null)
        }

        binding.btnSalvarLembrete.setOnClickListener {
            val titulo = binding.edtTituloLembrete.text.toString().trim()
            val descricao = binding.edtDescricaoLembrete.text.toString().trim()

            val prazoData = binding.edtPrazoData.text.toString().trim()
            val prazoHora = binding.edtPrazoHora.text.toString().trim()

            if (titulo.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            salvarNoFirestore(titulo, descricao, prazoData, prazoHora)
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun salvarNoFirestore(titulo: String, descricao: String, prazoData: String, prazoHora: String) {
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
            imageBase64 = imageBase64,
            date = dataAtual,
            time = horaAtual,
            prazoData = prazoData,
            prazoHora = prazoHora
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