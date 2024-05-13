package com.dicoding.subintermidiatesatu.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.subintermidiatesatu.R
import com.dicoding.subintermidiatesatu.view.welcome.WelcomeActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME)
    }

    companion object {
        private const val SPLASH_TIME: Long = 2000
    }
}