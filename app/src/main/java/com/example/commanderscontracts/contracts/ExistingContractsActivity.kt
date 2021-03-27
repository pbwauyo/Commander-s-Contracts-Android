package com.example.commanderscontracts.contracts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.commanderscontracts.R

class ExistingContractsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existing_contracts)

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

        //====Set title
        supportActionBar?.title = "Existing  Contracts"
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }
}