package com.example.diario_inteligente.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.model.Reminder
import com.example.diario_inteligente.databinding.ActivityEditReminderBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditReminderBinding
    private val db = FirebaseFirestore.getInstance()
    private var lembreteId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lembreteId = intent.getStringExtra("LEMBRETE_ID") ?: ""

        Log.d("STUDENT_LOG", "Carregando dados para edição do ID: $lembreteId")

        db.collection("reminders").document(lembreteId).get()
            .addOnSuccessListener { document ->
                val lembrete = document.toObject(Reminder::class.java)
                if (lembrete != null) {
                    binding.edtEditarTitulo.setText(lembrete.title)
                    binding.edtEditarDescricao.setText(lembrete.description)
                }
            }

        binding.btnSalvarAlteracao.setOnClickListener {
            val novoTitulo = binding.edtEditarTitulo.text.toString().trim()
            val novaDescricao = binding.edtEditarDescricao.text.toString().trim()

            if (novoTitulo.isEmpty() || novaDescricao.isEmpty()) {
                Toast.makeText(this, "Não deixe os campos vazios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("reminders").document(lembreteId)
                .update(
                    "title", novoTitulo,
                    "description", novaDescricao
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Lembrete atualizado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar.", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnExcluirLembrete.setOnClickListener {
            Log.d("STUDENT_LOG", "Excluindo o documento: $lembreteId")

            db.collection("reminders").document(lembreteId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Lembrete apagado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("STUDENT_LOG", "Falha ao deletar", e)
                    Toast.makeText(this, "Erro ao deletar do banco.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}