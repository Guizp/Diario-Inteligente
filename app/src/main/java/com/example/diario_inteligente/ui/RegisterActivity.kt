package com.example.diario_inteligente.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.databinding.ActivityRegisterBinding
import com.example.diario_inteligente.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNomeCadastro.text.toString().trim()
            val email = binding.edtEmailCadastro.text.toString().trim()
            val senha = binding.edtSenhaCadastro.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Todos os campos são obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha precisa ter no mínimo 6 dígitos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("CADASTRO_FLOW", "Criando usuário no Auth...")

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: ""
                    Log.d("CADASTRO_FLOW", "Usuário criado no Auth com sucesso. UID: $uid")

                    salvarUsuarioNoFirestore(uid, nome, email)
                }
                .addOnFailureListener { e ->
                    Log.e("CADASTRO_ERRO", "Falha no Auth", e)
                    Toast.makeText(this, "Erro ao criar conta: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun salvarUsuarioNoFirestore(uid: String, nome: String, email: String) {
        val novoUsuario = User(id = uid, name = nome, email = email)

        Log.d("CADASTRO_FIRESTORE", "Gravando dados adicionais na coleção 'users'")

        db.collection("users").document(uid)
            .set(novoUsuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Conta criada com sucesso! 🎉", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("CADASTRO_FIRESTORE_ERR", "Erro ao salvar no Firestore", e)
                Toast.makeText(this, "Conta criada, mas houve um problema ao salvar seu nome.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}