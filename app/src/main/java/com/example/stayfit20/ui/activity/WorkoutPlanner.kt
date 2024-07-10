package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
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
    private lateinit var goalSpinner: Spinner
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_planner)

        ageInput = findViewById(R.id.AgeInput)
        calorieInput = findViewById(R.id.Calorieinput)
        generateBtn = findViewById(R.id.generate_btn)
        goalSpinner = findViewById(R.id.goal_spinner)
        backButton = findViewById(R.id.back_button)

        val goals = resources.getStringArray(R.array.goals_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, goals)
        goalSpinner.adapter = adapter

        generateBtn.setOnClickListener {
            val age = ageInput.text.toString().toInt()
            val calories = calorieInput.text.toString().toInt()
            val goal = goalSpinner.selectedItem.toString()


            val backButton = findViewById<ImageButton>(R.id.back_button)
            backButton.setOnClickListener {
                finish() // Close the current activity and return to the previous activity
            }
            val intent = Intent(this, WorkoutPlannedView::class.java).apply {
                putExtra("AGE", age)
                putExtra("CALORIES", calories)
            }
            startActivity(intent)
        }
    }
}
