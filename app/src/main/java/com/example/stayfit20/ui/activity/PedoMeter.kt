package com.example.stayfit20.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.stayfit20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PedoMeter : AppCompatActivity(), SensorEventListener {

    private lateinit var tvStepCount: TextView
    private lateinit var tvStepGoal: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvTime: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button
    private lateinit var etStepGoal: EditText
    private lateinit var stepGoalBtn: ImageButton
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var isRunning = false
    private var stepCount: Long = 0
    private var stepGoal: Long = 0
    private var startStepCount: Long = 0
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private val handler = Handler()
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private var currentUserEmail: String? = null

    companion object {
        private const val STEP_LENGTH = 0.78 // Average step length in meters
        private const val LIGHT_THRESHOLD = 10.0 // Threshold for detecting low light
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedo_meter)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database.reference.child("pedometer_data")
        currentUserEmail = auth.currentUser?.email

        // Initialize views
        tvStepCount = findViewById(R.id.tv_step_count)
        tvStepGoal = findViewById(R.id.tv_step_goal)
        tvDistance = findViewById(R.id.tv_distance)
        tvTime = findViewById(R.id.tv_time)
        progressBar = findViewById(R.id.progressBar)
        btnStartPause = findViewById(R.id.btn_start_pause)
        btnReset = findViewById(R.id.btn_reset)
        etStepGoal = findViewById(R.id.et_step_goal)
        stepGoalBtn = findViewById(R.id.stepGoalBtn)

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

        stepGoalBtn.setOnClickListener {
            setStepGoal()
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous activity
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 100)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER && isRunning) {
            if (startStepCount == 0L) {
                startStepCount = event.values[0].toLong()
            }
            stepCount = event.values[0].toLong() - startStepCount
            updateUI()
        } else if (event.sensor.type == Sensor.TYPE_LIGHT && isRunning) {
            val lightLevel = event.values[0]
            if (lightLevel < LIGHT_THRESHOLD) {
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
            } else if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not implemented
    }

    private fun startPedometer() {
        isRunning = true
        startTime = SystemClock.elapsedRealtime() - pausedTime
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
        savePedometerData() // Save data when paused
    }

    private fun resetPedometer() {
        pausePedometer()
        stepCount = 0
        startStepCount = 0
        startTime = 0
        pausedTime = 0
        progressBar.progress = 0
        etStepGoal.text.clear()
        tvStepGoal.text = "Goal: 0 steps"
        tvTime.text = "00:00"  // Reset the time display to 00:00
        handler.post(runnable)  // Restart the runnable to update the time immediately
        updateUI()
    }

    private fun setStepGoal() {
        val stepGoalText = etStepGoal.text.toString()
        if (stepGoalText.isNotEmpty()) {
            stepGoal = stepGoalText.toLong()
            progressBar.max = stepGoal.toInt()
            tvStepGoal.text = "Goal: $stepGoal steps"
            updateUI()
        }
    }

    private fun updateUI() {
        tvStepCount.text = "Steps: $stepCount"
        tvDistance.text = String.format("Distance: %.2f m", stepCount * STEP_LENGTH)

        val progress = if (progressBar.max > 0) {
            ((stepCount.toDouble() / progressBar.max) * 100).toInt()
        } else {
            0
        }
        progressBar.progress = progress

        val elapsedMillis = SystemClock.elapsedRealtime() - startTime
        tvTime.text = formatTime(elapsedMillis)
    }

    @SuppressLint("DefaultLocale")
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
                handler.postDelayed(this, 100)
            } else {
                // Ensure time display updates immediately when reset
                tvTime.text = "00:00"
            }
        }
    }

    private fun savePedometerData() {
        val emailKey = currentUserEmail?.replace(".", ",") ?: return
        val pedometerData = PedometerData(
            stepCount = stepCount,
            stepGoal = stepGoal,
            elapsedTimeMillis = SystemClock.elapsedRealtime() - startTime,
            distance = stepCount * STEP_LENGTH
        )
        userReference.child(emailKey).setValue(pedometerData)
            .addOnSuccessListener {
                Log.i("PedoMeter", "Pedometer data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("PedoMeter", "Failed to save pedometer data: ${e.message}")
            }
    }

    data class PedometerData(
        val stepCount: Long,
        val stepGoal: Long,
        val elapsedTimeMillis: Long,
        val distance: Double
    )
}
