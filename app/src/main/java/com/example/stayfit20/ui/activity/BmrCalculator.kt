package com.example.stayfit20.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R

class BmrCalculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr_calculator)

        val ageInput = findViewById<EditText>(R.id.age_input)
        val heightInput = findViewById<EditText>(R.id.height_input)
        val weightInput = findViewById<EditText>(R.id.weight_input)
        val maleRadioButton = findViewById<RadioButton>(R.id.male_button)
        val femaleRadioButton = findViewById<RadioButton>(R.id.female_button)
        val calculateButton = findViewById<Button>(R.id.calculate_button)
        val bmrResultText = findViewById<TextView>(R.id.bmr_result_text)

        calculateButton.setOnClickListener {
            val ageText = ageInput.text.toString()
            val heightText = heightInput.text.toString()
            val weightText = weightInput.text.toString()

            if (ageText.isNotEmpty() && heightText.isNotEmpty() && weightText.isNotEmpty()) {
                val age = ageText.toInt()
                val height = heightText.toFloat()
                val weight = weightText.toFloat()

                // BMR calculation based on gender
                val bmr: Double = if (maleRadioButton.isChecked) {
                    // BMR calculation for males
                    (10 * weight) + (6.25 * height) - (5 * age) + 5
                } else {
                    // BMR calculation for females
                    (10 * weight) + (6.25 * height) - (5 * age) - 161
                }

                // Display BMR result on the TextView
                bmrResultText.text = "Your BMR is: $bmr"
            } else {
                // Show error message if any field is empty
                bmrResultText.text = "Please fill in all fields"
            }
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous activity
        }
    }
}


