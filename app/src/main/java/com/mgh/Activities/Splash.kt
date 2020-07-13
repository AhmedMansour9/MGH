package com.mgh.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import com.mgh.ChangeLanguage
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.View
import android.view.animation.AnimationUtils
import com.mgh.R


class Splash : AppCompatActivity() {
    private lateinit var DataSaver: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChangeLanguage.changeLang(this)
        setContentView(R.layout.activity_splash)
        DataSaver= PreferenceManager.getDefaultSharedPreferences(this)

        Img_logo.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        Img_logo.startAnimation(animation)

        Handler().postDelayed({
//            val UserToken: String? =DataSaver.getString("token",null);
//            if(UserToken!=null) {
                val intent = Intent(this, Navigation::class.java)
                startActivity(intent)
                finish()
//            }else {
//                val intent = Intent(this, Login::class.java)
//                startActivity(intent)
//                finish()
//            }
        }, 2500)


    }
}
