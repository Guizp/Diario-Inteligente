package com.example.diario_inteligente.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightSensorManager(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // callback que vai avisar a Activity se é para usar Modo Escuro (true) ou Modo Claro (false)
    private var onThemeChangeListener: ((Boolean) -> Unit)? = null

    // função para ligar o sensor e passar quem vai escutar a mudança
    fun startListening(listener: (Boolean) -> Unit) {
        onThemeChangeListener = listener
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // função para desligar o sensor e economizar bateria
    fun stopListening() {
        sensorManager.unregisterListener(this)
        onThemeChangeListener = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]

            // limiar de luminosidade (Threshold)
            // 10 Lux é um ambiente bem escurinho (quarto à noite ou luz apagada)
            if (lux < 50f) {
                // ambiente ESCURO
                onThemeChangeListener?.invoke(true)
            } else {
                // Ambiente CLARO
                onThemeChangeListener?.invoke(false)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //
    }
}