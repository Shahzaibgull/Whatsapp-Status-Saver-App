package com.example.taskwhatsappstatussaver

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taskwhatsappstatussaver.databinding.ActivityWhatsappSplashScreenBinding


@SuppressLint("CustomSplashScreen")
class WhatsappSplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWhatsappSplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWhatsappSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashImage.alpha=0f
        binding.splashImage.animate().setDuration(1500).alpha(1f).withEndAction{
            startActivity(Intent(this, WhatsappMainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}