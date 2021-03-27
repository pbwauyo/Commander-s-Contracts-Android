package com.example.commanderscontracts.registerloginresetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.commanderscontracts.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }
}