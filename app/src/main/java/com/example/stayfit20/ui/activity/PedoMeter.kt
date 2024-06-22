package com.example.stayfit20.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.os.SystemClock
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.stayfit20.R

class PedoMeter : AppCompatActivity(), SensorEventListener {

    private lateinit var tvStepCount: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvTime: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStartReset: Button
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var isRunning = false
    private var stepCount: Long = 0
    private var startTime: Long = 0
    private val handler = Handler()
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    companion object {
        private const val STEP_LENGTH = 0.78 // Average step length in meters
        private const val LIGHT_THRESHOLD = 10.0 // Threshold for detecting low light
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedo_meter)

        tvStepCount = findViewById(R.id.tv_step_count)
        tvDistance = findViewById(R.id.tv_distance)
        tvTime = findViewById(R.id.tv_time)
        progressBar = findViewById(R.id.progressBar)
        btnStartReset = findViewById(R.id.btn_start_reset)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "PedoMeter::ProximityWakeLock")

        btnStartReset.setOnClickListener {
            if (!isRunning) {
                startPedometer()
            } else {
                resetPedometer()
            }
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            PackageManager.PERMISSION_GRANTED
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isRunning) {
            when (event?.sensor?.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    stepCount = event.values[0].toLong()
                    updateUI()
                }
                Sensor.TYPE_LIGHT -> {
                    if (event.values[0] < LIGHT_THRESHOLD) {
                        wakeLock.acquire(10*60*1000L /*10 minutes*/)
                    } else if (wakeLock.isHeld) {
                        wakeLock.release()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not implemented
    }

    private fun startPedometer() {
        isRunning = true
        startTime = SystemClock.elapsedRealtime()
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
        btnStartReset.text = "Reset"
        handler.post(runnable)
    }

    private fun resetPedometer() {
        isRunning = false
        stepCount = 0
        startTime = 0
        sensorManager.unregisterListener(this)
        btnStartReset.text = "Start/Reset"
        handler.removeCallbacks(runnable)
        updateUI()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun updateUI() {
        tvStepCount.text = "Steps: $stepCount"
        tvDistance.text = String.format("Distance: %.2f m", stepCount * STEP_LENGTH)
        progressBar.progress = (stepCount % progressBar.max).toInt()
    }

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val elapsedMillis = SystemClock.elapsedRealtime() - startTime
                tvTime.text = String.format("Time: %d s", elapsedMillis / 1000)
                handler.postDelayed(this, 1000)
            }
        }
    }
}
