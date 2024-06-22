package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.stayfit20.R


class Dashboard : AppCompatActivity() {
    private lateinit var bmiBtn: CardView
    private lateinit var bmrBtn: CardView
    private lateinit var kcalBtn: CardView
    private lateinit var taskBtn: CardView
    private lateinit var setBtn: CardView
    private lateinit var workoutBtn: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        bmiBtn = findViewById(R.id.bmi_cal)
        bmrBtn = findViewById(R.id.bmr_cal)
        kcalBtn = findViewById(R.id.kcal_cal)
        taskBtn = findViewById(R.id.task_to_do)
        setBtn = findViewById(R.id.settings_card)
        workoutBtn = findViewById(R.id.workout_plan)


        bmiBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmiCalculator::class.java)
            startActivity(intent)
        }

        bmrBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmrCalculator::class.java)
            startActivity(intent)

        }
        kcalBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, PedoMeter::class.java)
            startActivity(intent)
        }

        taskBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, TaskViewActivity::class.java)
            startActivity(intent)
        }

        setBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, Settings_activity::class.java)
            startActivity(intent)

        }
        workoutBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, WorkoutPlanner::class.java)
            startActivity(intent)

        }


    }
}