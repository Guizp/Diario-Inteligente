package com.example.diario_inteligente.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.diario_inteligente.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("SPLASH_TESTE", "Splash iniciada. Carregando o app...")

        Handler(Looper.getMainLooper()).postDelayed({

            if (auth.currentUser != null) {
                Log.d("SPLASH_TESTE", "Usuário já logado. Redirecionando para Home.")
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Log.d("SPLASH_TESTE", "Usuário deslogado. Indo para o Login.")
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()
        }, 3000)
    }
}