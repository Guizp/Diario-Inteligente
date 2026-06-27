package com.example.diario_inteligente.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.converter.Base64Converter
import com.example.diario_inteligente.model.Reminder
import com.example.diario_inteligente.databinding.ActivityReminderDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class ReminderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private var lembreteId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lembreteId = intent.getStringExtra("LEMBRETE_ID") ?: ""

        Log.d("STUDENT_LOG", "Abrindo detalhes do lembrete ID: $lembreteId")

        if (lembreteId.isEmpty()) {
            Toast.makeText(this, "Erro ao abrir lembrete", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnIrParaEditar.setOnClickListener {
            val intent = Intent(this, EditReminderActivity::class.java)
            intent.putExtra("LEMBRETE_ID", lembreteId)
            startActivity(intent)
            finish()
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        carregarDadosDoLembrete()
    }

    private fun carregarDadosDoLembrete() {

        db.collection("reminders")
            .document(lembreteId)
            .get()
            .addOnSuccessListener { documentSnapshot ->

                val lembrete =
                    documentSnapshot.toObject(Reminder::class.java)

                if (lembrete != null) {

                    binding.txtDetalheTitulo.text =
                        lembrete.title

                    binding.txtDetalheDescricao.text =
                        lembrete.description

                    binding.txtDetalheDataHora.text =
                        "Criado em: ${lembrete.date} às ${lembrete.time}"

                    binding.txtDetalhePrazo.text = if (lembrete.prazoData.isNotEmpty() && lembrete.prazoHora.isNotEmpty()) {
                        "Prazo final: ${lembrete.prazoData} às ${lembrete.prazoHora}"
                    } else {
                        "Prazo final: Não definido"
                    }

                    if (lembrete.imageBase64.isNotEmpty()) {

                        binding.imgDetalheFoto.setImageBitmap(
                            Base64Converter.stringToBitmap(
                                lembrete.imageBase64
                            )
                        )
                    }
                }
            }
            .addOnFailureListener { e ->

                Log.e(
                    "STUDENT_LOG",
                    "Erro ao buscar detalhes no banco",
                    e
                )

                Toast.makeText(
                    this,
                    "Erro ao carregar dados.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}