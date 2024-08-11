package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R

class WorkoutPlanner : AppCompatActivity() {

    private lateinit var ageInput: EditText
    private lateinit var calorieInput: EditText
    private lateinit var generateBtn: Button
    private lateinit var myWorkout: Button
    private lateinit var goalSpinner: Spinner
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_planner)

        try {
            ageInput = findViewById(R.id.AgeInput)
            calorieInput = findViewById(R.id.Calorieinput)
            generateBtn = findViewById(R.id.generate_btn)
            goalSpinner = findViewById(R.id.goal_spinner)
            myWorkout = findViewById(R.id.plannedviewBtn)
            backButton = findViewById(R.id.back_button)

            val goals = resources.getStringArray(R.array.goals_array)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, goals)
            goalSpinner.adapter = adapter

            generateBtn.setOnClickListener {
                val ageText = ageInput.text.toString()
                val caloriesText = calorieInput.text.toString()

                if (ageText.isNotEmpty() && caloriesText.isNotEmpty()) {
                    try {
                        val age = ageText.toInt()
                        val calories = caloriesText.toInt()
                        val goal = goalSpinner.selectedItem.toString()

                        val intent = Intent(this, WorkoutPlannedView::class.java).apply {
                            putExtra("AGE", age)
                            putExtra("CALORIES", calories)
                        }
                        startActivity(intent)
                    } catch (e: NumberFormatException) {
                        Log.e("WorkoutPlanner", "Invalid number format", e)
                        // Optionally, show a user-friendly error message
                        ageInput.error = "Please enter a valid number"
                        calorieInput.error = "Please enter a valid number"
                    }
                } else {
                    // Show a user-friendly message if fields are empty
                    ageInput.error = "Please enter your age"
                    calorieInput.error = "Please enter calories"
                }
            }

            myWorkout.setOnClickListener {
                val intent = Intent(this, WorkoutPlannedView::class.java)
                startActivity(intent)
            }

            backButton.setOnClickListener {
                finish() // Close the current activity and return to the previous activity
            }

        } catch (e: Exception) {
            Log.e("WorkoutPlanner", "Error initializing activity", e)
        }
    }
}
