package com.example.debugunlockapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.debugunlockapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("debug_app", MODE_PRIVATE)
        binding.debugToggle.isChecked = prefs.getBoolean("debug_enabled", false)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

            if (username == "n0tstorm" && password == "STORM@3404") {
                prefs.edit().putBoolean("logged_in", true).apply()
                showUnlockedState()
                return@setOnClickListener
            }

            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
        }

        binding.devUnlockButton.setOnClickListener {
            val username = binding.usernameInput.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

            if (username == "n0tstorm" && password == "STORM@3404") {
                prefs.edit().putBoolean("debug_enabled", true).apply()
                prefs.edit().putBoolean("logged_in", true).apply()
                showUnlockedState()
            } else {
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }

        binding.debugToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("debug_enabled", isChecked).apply()
            if (isChecked) {
                prefs.edit().putBoolean("logged_in", true).apply()
                showUnlockedState()
            } else {
                prefs.edit().putBoolean("logged_in", false).apply()
                showLoginState()
            }
        }

        binding.logoutButton.setOnClickListener {
            prefs.edit().putBoolean("logged_in", false).apply()
            prefs.edit().putBoolean("debug_enabled", false).apply()
            binding.debugToggle.isChecked = false
            showLoginState()
        }

        binding.trialButton.setOnClickListener {
            prefs.edit().putBoolean("trial_unlocked", true).apply()
            showUnlockedState()
            Toast.makeText(this, "Trial started", Toast.LENGTH_SHORT).show()
        }

        binding.buyButton.setOnClickListener {
            prefs.edit().putBoolean("paid_unlocked", true).apply()
            showUnlockedState()
            Toast.makeText(this, "Premium unlocked", Toast.LENGTH_SHORT).show()
        }

        binding.demoModeButton.setOnClickListener {
            prefs.edit().putBoolean("demo_mode", true).apply()
            showUnlockedState(demo = true)
            Toast.makeText(this, getString(R.string.feature_demo_notice), Toast.LENGTH_SHORT).show()
        }

        binding.featureOneButton.setOnClickListener { handleFeatureAccess("Feature 1") }
        binding.featureTwoButton.setOnClickListener { handleFeatureAccess("Feature 2") }
        binding.featureThreeButton.setOnClickListener { handleFeatureAccess("Feature 3") }

        val isUnlocked = prefs.getBoolean("debug_enabled", false)
            || prefs.getBoolean("logged_in", false)
            || prefs.getBoolean("trial_unlocked", false)
            || prefs.getBoolean("paid_unlocked", false)
            || prefs.getBoolean("demo_mode", false)

        if (isUnlocked) {
            showUnlockedState(demo = prefs.getBoolean("demo_mode", false))
        } else {
            showLoginState()
        }
    }

    private fun handleFeatureAccess(featureName: String) {
        val prefs = getSharedPreferences("debug_app", MODE_PRIVATE)
        val unlocked = prefs.getBoolean("debug_enabled", false)
            || prefs.getBoolean("logged_in", false)
            || prefs.getBoolean("trial_unlocked", false)
            || prefs.getBoolean("paid_unlocked", false)
            || prefs.getBoolean("demo_mode", false)

        if (!unlocked) {
            showPaywallDialog()
            return
        }

        Toast.makeText(this, "$featureName unlocked", Toast.LENGTH_SHORT).show()
    }

    private fun showPaywallDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.paywall_title))
            .setMessage(getString(R.string.paywall_body))
            .setPositiveButton(getString(R.string.paywall_trial)) { _, _ ->
                getSharedPreferences("debug_app", MODE_PRIVATE).edit().putBoolean("trial_unlocked", true).apply()
                showUnlockedState() 
            }
            .setNegativeButton(getString(R.string.paywall_demo)) { _, _ ->
                getSharedPreferences("debug_app", MODE_PRIVATE).edit().putBoolean("demo_mode", true).apply()
                showUnlockedState(demo = true)
            }
            .setNeutralButton(getString(R.string.paywall_buy)) { _, _ ->
                getSharedPreferences("debug_app", MODE_PRIVATE).edit().putBoolean("paid_unlocked", true).apply()
                showUnlockedState()
            }
            .create()

        dialog.show()
    }

    private fun showLoginState() {
        binding.featuresContainer.visibility = View.GONE
        binding.paywallCard.visibility = View.VISIBLE
        binding.titleText.text = getString(R.string.login_title)
        binding.subtitleText.text = getString(R.string.login_subtitle)
        binding.statusText.text = "Login required"
        binding.statusText.setTextColor(getColor(android.R.color.holo_orange_dark))
        binding.usernameInput.setText("")
        binding.passwordInput.setText("")
    }

    private fun showUnlockedState(demo: Boolean = false) {
        binding.featuresContainer.visibility = View.VISIBLE
        binding.paywallCard.visibility = View.GONE
        binding.titleText.text = if (demo) "Demo access enabled" else "Access granted"
        binding.subtitleText.text = if (demo) getString(R.string.feature_demo_notice) else "All premium features are available in this debug build"
        binding.statusText.text = if (demo) "Demo mode" else "Premium unlocked"
        binding.statusText.setTextColor(getColor(android.R.color.holo_green_dark))
    }
}
