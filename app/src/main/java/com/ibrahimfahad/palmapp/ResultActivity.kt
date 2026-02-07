package com.ibrahimfahad.palmapp

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.ibrahimfahad.palmapp.databinding.ActivityResultBinding
import com.ibrahimfahad.palmapp.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val confidenceThreshold = 94f

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
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getParcelableExtra<Uri>("image_uri")
        val imagePath = intent.getStringExtra("image_path")

        if (imageUri != null) {
            val imageFile = imagePath?.let { File(it) }
            if (imageFile != null) {
                displayImage(imageFile)
                uploadImage(imageFile)
            }
        }

        binding.btnReset.setOnClickListener {
            finish()
        }
    }

    private fun displayImage(file: File) {
        binding.ivPreview.load(file)
        startLoader()
    }

    private fun startLoader() {
        binding.customLoader.loaderContainer.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(this, R.anim.bar_loading)
        binding.customLoader.bar1.startAnimation(anim)
        val anim2 = AnimationUtils.loadAnimation(this, R.anim.bar_loading)
        anim2.startOffset = 160
        binding.customLoader.bar2.startAnimation(anim2)
        val anim3 = AnimationUtils.loadAnimation(this, R.anim.bar_loading)
        anim3.startOffset = 320
        binding.customLoader.bar3.startAnimation(anim3)
    }

    private fun stopLoader() {
        binding.customLoader.loaderContainer.visibility = View.INVISIBLE
        binding.customLoader.bar1.clearAnimation()
        binding.customLoader.bar2.clearAnimation()
        binding.customLoader.bar3.clearAnimation()
    }

    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            try {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.predict(body)
                }

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val confidence = (result.confidence ?: 0f) * 100

                    if (confidence < confidenceThreshold) {
                        showLowConfidenceResult()
                        updatePredictionUI(null)
                    } else {
                        hideLowConfidenceResult()
                        updatePredictionUI(result.class_id)
                    }

                    updateConfidenceUI(confidence)

                } else {
                    Toast.makeText(this@ResultActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Connection Error", Toast.LENGTH_SHORT).show()
            } finally {
                stopLoader()
            }
        }
    }

    private fun updateConfidenceUI(confidence: Float) {
        binding.tvConfidence.text = String.format("%.1f%%", confidence)

        // Dynamic color: Red (Low) to Blue (High)
        val redColor = Color.parseColor("#F44336")
        val blueColor = Color.parseColor("#2196F3")
        val ratio = (confidence / 100f).coerceIn(0f, 1f)

        val dynamicColor = ColorUtils.blendARGB(redColor, blueColor, ratio)
        binding.tvConfidence.setTextColor(dynamicColor)

        // Add a small entrance animation for the text
        binding.tvConfidence.alpha = 0f
        binding.tvConfidence.animate().alpha(1f).setDuration(500).start()
    }

    private fun updatePredictionUI(classId: Int?) {
        val popAnim = AnimationUtils.loadAnimation(this, R.anim.pop_up)

        // Reset all to inactive style
        val views = listOf(binding.resKhalas, binding.resShishi, binding.resRazzez)
        views.forEach {
            it.setBackgroundResource(R.drawable.prediction_item_inactive)
            it.setTextColor(Color.parseColor("#28292c"))
        }

        // Highlight predicted item
        when (classId) {
            0 -> highlightView(binding.resKhalas, popAnim)
            1 -> highlightView(binding.resRazzez, popAnim)
            2 -> highlightView(binding.resShishi, popAnim)
        }
    }

    private fun showLowConfidenceResult() {
        binding.tvResult.text = "its not one of Khalas,Shishi,Razzez"
        binding.tvResult.visibility = View.VISIBLE
        binding.predictionContainer.visibility = View.GONE
    }

    private fun hideLowConfidenceResult() {
        binding.tvResult.visibility = View.GONE
        binding.predictionContainer.visibility = View.VISIBLE
    }

    private fun highlightView(view: TextView, animation: android.view.animation.Animation) {
        view.setBackgroundResource(R.drawable.prediction_item_highlight)
        view.setTextColor(Color.WHITE)
        view.startAnimation(animation)
    }
}