package com.example.diario_inteligente.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.converter.Base64Converter
import com.example.diario_inteligente.databinding.ActivityEditReminderBinding
import com.example.diario_inteligente.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore

class EditReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditReminderBinding
    private val db = FirebaseFirestore.getInstance()
    private var lembreteId: String = ""
    private var imageBase64 = ""

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->

            bitmap?.let {

                binding.imgEditarFoto.setImageBitmap(it)

                imageBase64 =
                    Base64Converter.bitmapToString(it)
            }
        }

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

                    binding.edtEditarPrazoData.setText(lembrete.prazoData)
                    binding.edtEditarPrazoHora.setText(lembrete.prazoHora)

                    imageBase64 = lembrete.imageBase64

                    if (imageBase64.isNotEmpty()) {

                        binding.imgEditarFoto.setImageBitmap(
                            Base64Converter.stringToBitmap(imageBase64)
                        )
                    }
                }
            }

        binding.btnAlterarFoto.setOnClickListener {
            cameraLauncher.launch(null)
        }

        binding.btnSalvarAlteracao.setOnClickListener {

            val novoTitulo =
                binding.edtEditarTitulo.text.toString().trim()

            val novaDescricao =
                binding.edtEditarDescricao.text.toString().trim()

            val novoPrazoData = binding.edtEditarPrazoData.text.toString().trim()
            val novoPrazoHora = binding.edtEditarPrazoHora.text.toString().trim()

            if (novoTitulo.isEmpty() || novaDescricao.isEmpty() || novoPrazoData.isEmpty() || novoPrazoHora.isEmpty()) {

                Toast.makeText(
                    this,
                    "Não deixe os campos vazios!",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            db.collection("reminders")
                .document(lembreteId)
                .update(
                    "title", novoTitulo,
                    "description", novaDescricao,
                    "imageBase64", imageBase64,
                    "prazoData", novoPrazoData,
                    "prazoHora", novoPrazoHora
                )
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Lembrete atualizado!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Erro ao atualizar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        binding.btnExcluirLembrete.setOnClickListener {

            Log.d(
                "STUDENT_LOG",
                "Excluindo o documento: $lembreteId"
            )

            db.collection("reminders")
                .document(lembreteId)
                .delete()
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Lembrete apagado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener { e ->

                    Log.e(
                        "STUDENT_LOG",
                        "Falha ao deletar",
                        e
                    )

                    Toast.makeText(
                        this,
                        "Erro ao deletar do banco.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }
}