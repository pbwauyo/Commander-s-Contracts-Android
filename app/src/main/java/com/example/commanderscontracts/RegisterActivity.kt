package com.example.commanderscontracts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)

        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    fun back(view: View?) {
        finishMyActivity()
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }
}