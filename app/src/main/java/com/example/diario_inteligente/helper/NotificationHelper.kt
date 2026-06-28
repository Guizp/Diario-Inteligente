package com.example.diario_inteligente.helper

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.diario_inteligente.model.Reminder
import com.example.diario_inteligente.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val TAG = "NotificationHelper"

    companion object {
        const val CHANNEL_ID = "smart_reminder_channel"
        const val CHANNEL_NAME = "Lembretes do Smart-Reminder"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para avisos de prazos dos lembretes"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun agendarNotificacao(lembrete: Reminder, nomeUsuario: String) {
        if (lembrete.prazoData.isEmpty() || lembrete.prazoHora.isEmpty()) return

        try {
            val formatador = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dataTexto = "${lembrete.prazoData} ${lembrete.prazoHora}"
            val dataPrazoFinal = formatador.parse(dataTexto) ?: return

            val calendar = Calendar.getInstance()
            calendar.time = dataPrazoFinal
            val tempoPrazoFinalMillis = calendar.timeInMillis

            val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val minutosAntecedencia = preferences.getInt("timer_minimo_lembrete", 5)
            val milissegundosSubtrair = minutosAntecedencia * 60L * 1000L

            val tempoAlarmeMillis = tempoPrazoFinalMillis - milissegundosSubtrair

            if (tempoAlarmeMillis <= System.currentTimeMillis()) {
                Log.d(TAG, "Horario do alarme ja passou.")
                return
            }

            // Usando Intent explicita para apontar direto para a classe do Receiver
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("REMINDER_TITLE", lembrete.title)
                putExtra("REMINDER_TIME", lembrete.prazoHora)
                putExtra("USER_NAME", nomeUsuario)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                lembrete.id.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intentConfig = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intentConfig.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intentConfig)
                    Log.d(TAG, "Permissao de alarme exato necessaria. Abrindo configuracoes.")
                    return
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tempoAlarmeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    tempoAlarmeMillis,
                    pendingIntent
                )
            }

            Log.d(TAG, "Alarme definido com sucesso.")

        } catch (e: SecurityException) {
            Log.e(TAG, "Erro de permissao de alarme", e)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao processar agendamento", e)
        }
    }

    @SuppressLint("MissingPermission", "POST_NOTIFICATIONS")
    fun dispararNotificacaoVisual(tituloReminder: String?, horario: String, nomeUsuario: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val lembreteTitulo = tituloReminder ?: "Lembrete"

        val pendingIntent = PendingIntent.getActivity(
            context,
            lembreteTitulo.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val corpoTexto =
            "Hey! $nomeUsuario, o seu Reminder \"$lembreteTitulo\" vai expirar às $horario! Fique ligado!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Prazo Expirando! ⏰")
            .setContentText(corpoTexto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(corpoTexto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        // Use uma tag String junto com o ID para garantir unicidade e evitar erros de tipo
        val tag = "REMINDER_TAG"
        val idNotificacao = lembreteTitulo.hashCode()

        try {
            // Tentativa de disparo usando tag e ID
            notificationManager.notify(tag, idNotificacao, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Erro fatal ao disparar notificação: ${e.message}")
        }
    }
}