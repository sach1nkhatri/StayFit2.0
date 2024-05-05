package com.example.stayfit20

// Inside your CalorieCalculator activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalorieCalculator : AppCompatActivity() {
    private lateinit var ageInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var calorieResultText: TextView
    private lateinit var genderSelector: RadioGroup
    private lateinit var backButton: ImageButton // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_calculator)

        ageInput = findViewById(R.id.age_input)
        heightInput = findViewById(R.id.height_input)
        weightInput = findViewById(R.id.weight_input)
        calculateButton = findViewById(R.id.calculate_button)
        calorieResultText = findViewById(R.id.calorie_result_text)
        genderSelector = findViewById(R.id.gender_selector)
        backButton = findViewById(R.id.back_button) // Initialize the backButton

        calculateButton.setOnClickListener {
            calculateCalorieIntake()
        }

        // Set OnClickListener for the backButton
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    // This function calculates the calorie intake based on the provided inputs
    private fun calculateCalorieIntake() {
        val age = ageInput.text.toString().toIntOrNull()
        val height = heightInput.text.toString().toDoubleOrNull()
        val weight = weightInput.text.toString().toDoubleOrNull()

        if (age != null && height != null && weight != null) {
            val genderId = genderSelector.checkedRadioButtonId
            val gender = findViewById<RadioButton>(genderId).text.toString()

            val bmr = if (gender == getString(R.string.male)) {
                88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
            } else {
                447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
            }

            // Calculate calorie intake (BMR * activity factor)
            // You might want to introduce activity factor based on user's activity level

            calorieResultText.text = getString(R.string.calorie_result, bmr.toInt())
        } else {
            calorieResultText.text = getString(R.string.invalid_input)
        }
    }
}


