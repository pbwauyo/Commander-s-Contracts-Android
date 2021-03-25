package com.example.commanderscontracts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_btn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)
        }


        login_btn_main.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)

            Log.d("MainActivity", "Main Activity")
        }


    }
}