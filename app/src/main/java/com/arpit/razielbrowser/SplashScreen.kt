package com.arpit.razielbrowser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        icLogo.startAnimation(AnimationUtils.loadAnimation(this , R.anim.splash_in))
        Handler().postDelayed({
            icLogo.startAnimation(AnimationUtils.loadAnimation(this , R.anim.splash_out))
            Handler().postDelayed({
                icLogo.visibility = View.GONE
                startActivity(Intent(this , MainActivity::class.java))
                finish()
            } , 500)
        } , 1500)
    }
}