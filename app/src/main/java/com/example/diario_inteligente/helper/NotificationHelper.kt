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
            val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

            val notificacoesAtivas = preferences.getBoolean("notificacoes", true)
            if (!notificacoesAtivas) {
                Log.d(TAG, "Agendamento cancelado: Notificações desativadas nas configurações.")
                return
            }

            val formatador = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
                timeZone = java.util.TimeZone.getDefault()
            }
            val dataTexto = "${lembrete.prazoData} ${lembrete.prazoHora}"
            val dataPrazoFinal = formatador.parse(dataTexto) ?: return
            val calendar = Calendar.getInstance()
            calendar.time = dataPrazoFinal
            val tempoPrazoFinalMillis = calendar.timeInMillis
            val minutosAntecedencia = preferences.getInt("timer_minimo_lembrete", 5)
            val milissegundosSubtrair = minutosAntecedencia * 60L * 1000L
            var tempoAlarmeMillis = tempoPrazoFinalMillis - milissegundosSubtrair
            val agora = System.currentTimeMillis()

            if (tempoAlarmeMillis <= agora) {

                // se o momento de antecedência já passou, mas o prazo FINAL ainda está no futuro,
                // podemos ajustar para o alarme tocar exatamente no prazo final.
                if (tempoPrazoFinalMillis > agora) {
                    Log.d(TAG, "Antecedência de $minutosAntecedencia min já passou para [${lembrete.title}]. Agendando para o prazo exato.")
                    tempoAlarmeMillis = tempoPrazoFinalMillis
                } else {
                    Log.d(TAG, "O prazo final do lembrete [${lembrete.title}] já expirou. Ignorando.")
                    return
                }
            }

            val dataFormatadaTeste = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(tempoAlarmeMillis)
            Log.d(TAG, "Agendando \"${lembrete.title}\" para tocar em: $dataFormatadaTeste (Antecedência: $minutosAntecedencia min)")

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("REMINDER_TITLE", lembrete.title)
                putExtra("REMINDER_TIME", lembrete.prazoHora)
                putExtra("USER_NAME", nomeUsuario)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                lembrete.id.hashCode(), // Garante que cada lembrete tenha seu próprio alarme único
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // checagem de permissão para Android 12+ (Schedule Exact Alarms)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intentConfig = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intentConfig.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intentConfig)
                    Log.w(TAG, "Permissão de alarme exato necessária. Abrindo configurações do sistema.")
                    return
                }
            }

            // configuração do alarme preciso de acordo com a versão do Android
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

            Log.d(TAG, "Alarme para [${lembrete.title}] definido com sucesso.")

        } catch (e: SecurityException) {
            Log.e(TAG, "Erro de permissão de alarme (SecurityException)", e)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao processar agendamento do lembrete", e)
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