package com.example.stayfit20.ui.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.stayfit20.R

class BmiCalculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bmi_calculator)

        val ageInput = findViewById<EditText>(R.id.age_input)
        val heightInput = findViewById<EditText>(R.id.height_input)
        val weightInput = findViewById<EditText>(R.id.weight_input)
        val calculateButton = findViewById<Button>(R.id.calculate_button)
        val bmiResultText = findViewById<TextView>(R.id.bmi_result_text)
        val bmiCategoryText = findViewById<TextView>(R.id.bmi_category_text)
        val maleRadioButton = findViewById<RadioButton>(R.id.male_button)
        val femaleRadioButton = findViewById<RadioButton>(R.id.female_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        calculateButton.setOnClickListener {
            val ageText = ageInput.text.toString()
            val heightText = heightInput.text.toString()
            val weightText = weightInput.text.toString()

            if (ageText.isNotEmpty() && heightText.isNotEmpty() && weightText.isNotEmpty()) {
                val age = ageText.toInt()
                val heightInCM = heightText.toFloat()
                val weightInKG = weightText.toFloat()

                val heightInMeters = heightInCM / 100

                val gender = if (maleRadioButton.isChecked) "male" else "female"

                val bmi = if (gender == "male") {
                    1.3f * (weightInKG / (heightInMeters * heightInMeters)) - 13
                } else {
                    1.3f * (weightInKG / (heightInMeters * heightInMeters)) - 11
                }

                bmiResultText.text = "Your BMI is: $bmi"

                val bmiCategory = when {
                    bmi < 18.5 -> "Underweight"
                    bmi >= 18.5 && bmi < 25 -> "Normal weight"
                    bmi >= 25 && bmi < 30 -> "Overweight"
                    else -> "Obesity"
                }

                bmiCategoryText.text = "BMI Category: $bmiCategory"
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish() // Finish the current activity to return to the main activity
        }
    }
}


