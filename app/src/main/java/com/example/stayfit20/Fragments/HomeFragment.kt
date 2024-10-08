package com.example.stayfit20.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.example.stayfit20.R
import com.example.stayfit20.ui.activity.BmiCalculator
import com.example.stayfit20.ui.activity.BmrCalculator
import com.example.stayfit20.ui.activity.PedoMeter
import com.example.stayfit20.ui.activity.WorkoutPlanner

class HomeFragment : Fragment() {

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Handle card clicks
        val bmiCard: CardView = view.findViewById(R.id.bmi_cal)
        bmiCard.setOnClickListener {
            val intent = Intent(activity, BmiCalculator::class.java)
            startActivity(intent)
        }

        val bmrCard: CardView = view.findViewById(R.id.BMR_cal)
        bmrCard.setOnClickListener {
            val intent = Intent(activity, BmrCalculator::class.java)
            startActivity(intent)
        }

        val workoutPlanCard: CardView = view.findViewById(R.id.workout_plan)
        workoutPlanCard.setOnClickListener {
            val intent = Intent(activity, WorkoutPlanner::class.java)
            startActivity(intent)
        }

        val pedometerCard: CardView = view.findViewById(R.id.Pedometer)
        pedometerCard.setOnClickListener {
            val intent = Intent(activity, PedoMeter::class.java)
            startActivity(intent)
        }



        return view
    }


    }
