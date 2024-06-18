package com.infiren.crispyapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.infiren.crispyapp.R
import com.infiren.crispyapp.util.HelpPreferenceManager

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 1000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)

        Handler(Looper.getMainLooper()).postDelayed({
//            if (HelpPreferenceManager.isFirstLaunch(this)) {
//                val intent = Intent(this, HelpActivity::class.java)
//                HelpPreferenceManager.setFirstLaunch(this, false)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            }
            //Для дебага
            val intent = Intent(this, HelpActivity::class.java)
            HelpPreferenceManager.setFirstLaunch(this, false)
            startActivity(intent)

            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}
