package com.example.stayfit20

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class WorkoutPlanner : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_planner)
        enableEdgeToEdge()

        val goalSpinner: Spinner = findViewById(R.id.goal_spinner)

        // Create an ArrayAdapter using the custom layout for the dropdown items
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.goals_array,
            R.layout.spinner_item
        )

        // Specify the custom layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item)

        // Apply the adapter to the spinner
        goalSpinner.adapter = adapter

        // Set an OnItemSelectedListener to handle the default prompt display
        goalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // No action needed, just handling the default prompt display
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }
}
