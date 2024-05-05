package com.example.stayfit20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class Dashboard : AppCompatActivity() {
    private lateinit var bmiBtn: CardView
    private lateinit var bmrBtn: CardView
    private lateinit var kcalBtn: CardView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        bmiBtn = findViewById(R.id.bmi_cal)
        bmrBtn = findViewById(R.id.bmr_cal)
        kcalBtn = findViewById(R.id.kcal_cal)

        bmiBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmiCalculator::class.java)
            startActivity(intent)
        }

        bmrBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmrCalculator::class.java)
            startActivity(intent)

        }
        kcalBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, CalorieCalculator::class.java)
            startActivity(intent)
        }

    }

}