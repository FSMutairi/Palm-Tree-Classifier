package com.ibrahimfahad.palmapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import com.ibrahimfahad.palmapp.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("APP_LANGUAGE", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        updateLangButtonText()

        // Set initial state of the switch and listen for changes
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("DARK_MODE", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("DARK_MODE", false).apply()
            }
        }

        binding.btnSwitchLang.setOnClickListener {
            switchLanguage()
        }

        // CNN Info button listener
        binding.btnCnnInfo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.geeksforgeeks.org/deep-learning/convolutional-neural-network-cnn-in-machine-learning/"))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun updateLangButtonText() {
        val currentLang = sharedPreferences.getString("APP_LANGUAGE", "en")
        binding.btnSwitchLang.text = if (currentLang == "en") getString(R.string.switch_to_arabic) else getString(R.string.switch_to_english)
    }

    private fun switchLanguage() {
        val currentLang = sharedPreferences.getString("APP_LANGUAGE", "en")
        val newLang = if (currentLang == "en") "ar" else "en"

        sharedPreferences.edit().putString("APP_LANGUAGE", newLang).apply()

        // Restart application to apply language changes to all activities
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
