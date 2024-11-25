package com.oym.libraryapi.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oym.libraryapi.R
import com.oym.libraryapi.databinding.ActivityMainBinding
import com.oym.libraryapi.databinding.ActivitySplashBinding
import com.oym.libraryapi.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // Espera 2 segundos
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish() // Cierra la actividad de splash
        }

    }
}