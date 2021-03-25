package com.example.commanderscontracts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewContractActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contract)

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)

        //====Set title
        supportActionBar?.title = "New Contract"


    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }
}