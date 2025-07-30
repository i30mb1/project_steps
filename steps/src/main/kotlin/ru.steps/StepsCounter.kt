package ru.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var totalSteps: Int = 0

    fun startCounting() {
        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    fun stopCounting() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = event.values[0].toInt()
        }
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        println("sdf")
    }

}