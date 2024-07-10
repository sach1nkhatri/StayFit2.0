package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.stayfit20.Fragments.HomeFragment
import com.example.stayfit20.Fragments.SettingsFragment
import com.example.stayfit20.R
import com.example.stayfit20.Fragments.TaskFragment
import com.example.stayfit20.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        // Set up bottom navigation view listener
        binding.buttonNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeNav -> {
                    loadFragment(HomeFragment(), getString(R.string.home))
                    true
                }
                R.id.TaskNav -> {
                    loadFragment(TaskFragment(), getString(R.string.task))
                    true
                }
                R.id.settingsNav -> {
                    loadFragment(SettingsFragment(), getString(R.string.settings))
                    true
                }
                else -> false
            }
        }

        // Set click listener for the drawer button
        binding.slideMenuBtn.setOnClickListener {
            openDrawer()
        }

        // Set up navigation item click listener for NavigationView
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.WorkoutPlannerNav -> {
                    navigateToActivity(WorkoutPlanner::class.java)
                    true
                }
                R.id.BMI_nav -> {
                    navigateToActivity(BmiCalculator::class.java)
                    true
                }
                R.id.BMR_nav -> {
                    navigateToActivity(BmrCalculator::class.java)
                    true
                }
                R.id.Pedometer_nav -> {
                    navigateToActivity(PedoMeter::class.java)
                    true
                }
                R.id.Log_Out -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), getString(R.string.home))
        }
    }

    private fun navigateToActivity(cls: Class<*>) {
        val intent = Intent(this@Dashboard, cls)
        startActivity(intent)
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
        updateTitle(title)
    }

    private fun updateTitle(title: String) {
        binding.textViewDashboard.text = title
    }

    private fun openDrawer() {
        Log.d("Dashboard", "openDrawer called")
        binding.drawerLayout.openDrawer(binding.navigationView)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which ->
            auth.signOut()
            navigateToLogin()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
