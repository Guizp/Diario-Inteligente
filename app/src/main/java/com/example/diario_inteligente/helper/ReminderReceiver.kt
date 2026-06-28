package com.example.diario_inteligente.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "AlarmManager chamou o receiver com sucesso")

        val titulo = intent.getStringExtra("REMINDER_TITLE") ?: "Lembrete"
        val horario = intent.getStringExtra("REMINDER_TIME") ?: "--:--"
        val nomeUsuario = intent.getStringExtra("USER_NAME") ?: "Usuario"

        try {
            val helper = NotificationHelper(context)
            helper.createNotificationChannel()
            helper.dispararNotificacaoVisual(titulo, horario, nomeUsuario)
        } catch (e: Exception) {
            Log.e("ReminderReceiver", "Falha ao exibir notificacao", e)
        }
    }
}