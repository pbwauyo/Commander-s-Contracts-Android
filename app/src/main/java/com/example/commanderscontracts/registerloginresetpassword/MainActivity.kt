package com.example.commanderscontracts.registerloginresetpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.example.commanderscontracts.R
import com.example.commanderscontracts.UserPreferences
import com.example.commanderscontracts.contracts.NewOrExistingContracts
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val userPref = UserPreferences(this)

        userPref.isLoggedIn.asLiveData().observe(this, Observer{


            Toast.makeText(this,"Token is ${it}", Toast.LENGTH_LONG).show()

            if(it == true) {

                val intent = Intent(this, NewOrExistingContracts::class.java)
//               //clear all activities on the stack
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else {

                val intent = Intent(this, LoginActivity::class.java)
//               //clear all activities on the stack
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }

        })

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