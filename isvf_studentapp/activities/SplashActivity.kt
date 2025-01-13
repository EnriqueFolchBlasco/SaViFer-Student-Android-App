package es.efb.isvf_studentapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import es.efb.isvf_studentapp.R
import kotlin.concurrent.schedule
import java.util.Timer

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //Aniamcioo
        val logo = findViewById<ImageView>(R.id.imageView)
        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        logo.startAnimation(slideDownAnimation)

        Timer().schedule(2500) {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)

            // Custom la transciaos per a capa dalt
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
            finish()
        }
    }
}
