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
import android.widget.EditText
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
    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button
    private lateinit var etStepGoal: EditText
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var isRunning = false
    private var stepCount: Long = 0
    private var startStepCount: Long = 0
    private var startTime: Long = 0
    private var pausedTime: Long = 0
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
        btnStartPause = findViewById(R.id.btn_start_pause)
        btnReset = findViewById(R.id.btn_reset)
        etStepGoal = findViewById(R.id.et_step_goal)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "PedoMeter::ProximityWakeLock")

        btnStartPause.setOnClickListener {
            if (!isRunning) {
                startPedometer()
            } else {
                pausePedometer()
            }
        }

        btnReset.setOnClickListener {
            resetPedometer()
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
                    if (startStepCount == 0L) {
                        startStepCount = event.values[0].toLong()
                    }
                    stepCount = event.values[0].toLong() - startStepCount
                    updateUI()
                }
                Sensor.TYPE_LIGHT -> {
                    if (event.values[0] < LIGHT_THRESHOLD) {
                        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
                    } else if (wakeLock.isHeld) {
                        wakeLock.release()
                    }
                }
                // Handle other sensors if needed
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not implemented
    }

    private fun startPedometer() {
        val stepGoalText = etStepGoal.text.toString()
        if (stepGoalText.isNotEmpty()) {
            val stepGoal = stepGoalText.toInt()
            progressBar.max = stepGoal
        }

        isRunning = true
        startTime = if (pausedTime == 0L) {
            SystemClock.elapsedRealtime()
        } else {
            SystemClock.elapsedRealtime() - pausedTime
        }

        startStepCount = 0L
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
        btnStartPause.text = "Pause"
        handler.post(runnable)
    }

    private fun pausePedometer() {
        isRunning = false
        pausedTime = SystemClock.elapsedRealtime() - startTime
        sensorManager.unregisterListener(this)
        btnStartPause.text = "Start"
        handler.removeCallbacks(runnable)
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun resetPedometer() {
        pausePedometer()
        stepCount = 0
        startStepCount = 0
        startTime = 0
        pausedTime = 0
        updateUI()
    }

    private fun updateUI() {
        tvStepCount.text = "Steps: $stepCount"
        tvDistance.text = String.format("Distance: %.2f m", stepCount * STEP_LENGTH)

        // Calculate progress percentage based on current step count
        val progress = if (progressBar.max > 0) {
            ((stepCount.toDouble() / progressBar.max) * 100).toInt()
        } else {
            0
        }
        progressBar.progress = progress

        val elapsedMillis = SystemClock.elapsedRealtime() - startTime
        tvTime.text = formatTime(elapsedMillis)
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val elapsedMillis = SystemClock.elapsedRealtime() - startTime
                tvTime.text = formatTime(elapsedMillis)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        sensorManager.unregisterListener(this)
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}
