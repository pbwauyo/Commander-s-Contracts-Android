package com.example.commanderscontracts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_new_or_existing_contracts.*


class NewOrExistingContracts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_or_existing_contracts)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)


        //=====set the title====
        //supportActionBar?.title = "Commander's Contracts"

        verifyUserIsLoggedIn()


        create_new_contract_btn.setOnClickListener {

            val intent = Intent(this,NewContractActivity::class.java)
            startActivity(intent)

        }



        existing_contracts_btn.setOnClickListener {

            val intent = Intent(this,ExistingContractsActivity::class.java)
            startActivity(intent)

        }
    }




    //=========CHECKED IF USER IS LOGGED IN ========
    private fun verifyUserIsLoggedIn() {
        //===check if user is logged in ====

        val uid = FirebaseAuth.getInstance().uid


        if(uid == null) {
            //====user not logged in, redirect to register ====
            val intent = Intent(this,MainActivity::class.java)
            //clear all activities on the stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }

    }



    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }

}