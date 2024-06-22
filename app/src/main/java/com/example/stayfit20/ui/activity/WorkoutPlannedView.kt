package com.example.stayfit20.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R

class WorkoutPlannedView : AppCompatActivity() {

    private lateinit var workoutPlanTextView: TextView
    private lateinit var dietPlanTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_planned_view)

        workoutPlanTextView = findViewById(R.id.workout_plan_text_view)
        dietPlanTextView = findViewById(R.id.diet_plan_text_view)

        val bundle = intent.extras
        if (bundle != null) {
            val age = bundle.getInt("AGE")
            val calories = bundle.getInt("CALORIES")

            val workoutPlan = getWorkoutPlan(age)
            val dietPlan = getDietPlan(calories)

            workoutPlanTextView.text = formatPlanText(workoutPlan)
            dietPlanTextView.text = formatPlanText(dietPlan)
        }
    }

    private fun getWorkoutPlan(age: Int): Map<String, String> {
        return when (age) {
            in 18..29 -> mapOf(
                "Sunday" to getString(R.string.age_18_29_sunday),
                "Monday" to getString(R.string.age_18_29_monday),
                "Tuesday" to getString(R.string.age_18_29_tuesday),
                "Wednesday" to getString(R.string.age_18_29_wednesday),
                "Thursday" to getString(R.string.age_18_29_thursday),
                "Friday" to getString(R.string.age_18_29_friday),
                "Saturday" to getString(R.string.age_18_29_saturday)
            )
            in 30..49 -> mapOf(
                "Sunday" to getString(R.string.age_30_49_sunday),
                "Monday" to getString(R.string.age_30_49_monday),
                "Tuesday" to getString(R.string.age_30_49_tuesday),
                "Wednesday" to getString(R.string.age_30_49_wednesday),
                "Thursday" to getString(R.string.age_30_49_thursday),
                "Friday" to getString(R.string.age_30_49_friday),
                "Saturday" to getString(R.string.age_30_49_saturday)
            )
            in 50..100 -> mapOf(
                "Sunday" to getString(R.string.age_50_sunday),
                "Monday" to getString(R.string.age_50_monday),
                "Tuesday" to getString(R.string.age_50_tuesday),
                "Wednesday" to getString(R.string.age_50_wednesday),
                "Thursday" to getString(R.string.age_50_thursday),
                "Friday" to getString(R.string.age_50_friday),
                "Saturday" to getString(R.string.age_50_saturday)
            )
            else -> emptyMap()
        }
    }

    private fun getDietPlan(calories: Int): Map<String, String> {
        return when (calories) {
            in 1500..2200 -> mapOf(
                "Breakfast" to getString(R.string.diet_1500_2200_breakfast),
                "Lunch" to getString(R.string.diet_1500_2200_lunch),
                "Dinner" to getString(R.string.diet_1500_2200_dinner),
                "Snacks" to getString(R.string.diet_1500_2200_snacks)
            )
            in 2200..3000 -> mapOf(
                "Breakfast" to getString(R.string.diet_2200_3000_breakfast),
                "Lunch" to getString(R.string.diet_2200_3000_lunch),
                "Dinner" to getString(R.string.diet_2200_3000_dinner),
                "Snacks" to getString(R.string.diet_2200_3000_snacks)
            )
            else -> mapOf(
                "Breakfast" to getString(R.string.diet_3500_plus_breakfast),
                "Lunch" to getString(R.string.diet_3500_plus_lunch),
                "Dinner" to getString(R.string.diet_3500_plus_dinner),
                "Snacks" to getString(R.string.diet_3500_plus_snacks)
            )
        }
    }

    private fun formatPlanText(plan: Map<String, String>): String {
        return plan.entries.joinToString("\n\n") { (key, value) ->
            "$key:\n${value.lines().joinToString("\n") { "• $it" }}"
        }
    }
}
