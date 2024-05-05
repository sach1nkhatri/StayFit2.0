package com.example.stayfit20

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class Dashboard : AppCompatActivity() {
    private lateinit var bmiBtn: CardView
    private lateinit var bmrBtn: CardView
//    lateinit var calBtn: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        bmiBtn = findViewById(R.id.bmi_cal)
        bmrBtn = findViewById(R.id.bmr_cal)
//        calBtn = findViewById(R.id.cal_calculator)

        bmiBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmiCalculator::class.java)
            startActivity(intent)
        }

        bmrBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, BmrCalculator::class.java)
            startActivity(intent)

        }

//        calBtn.setOnClickListener {
//            val intent = Intent(this@Dashboard, CalorieCalculator::class.java)
//            startActivity(intent)
//        }

    }

}