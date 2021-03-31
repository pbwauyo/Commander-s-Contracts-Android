package com.example.commanderscontracts.contracts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.commanderscontracts.CaptureSignaturesActivity
import com.example.commanderscontracts.registerloginresetpassword.MainActivity
import com.example.commanderscontracts.R
import com.example.commanderscontracts.UserPreferences
import com.example.commanderscontracts.registerloginresetpassword.LoginActivity
import com.example.commanderscontracts.registerloginresetpassword.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_new_or_existing_contracts.*
import kotlinx.coroutines.launch


class NewOrExistingContracts : AppCompatActivity() {

    //ON BACK PRESS TO EXIT AN APP FROM THE DETAILS ACTIVITY
    private var doubleBackToExitPressedOnce = false
    private lateinit var backToast: Toast

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_or_existing_contracts)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)

        userPreferences = UserPreferences(this)


        //=====set the title====
        //supportActionBar?.title = "Commander's Contracts"

        verifyUserIsLoggedIn()


        create_new_contract_btn.setOnClickListener {

            val intent = Intent(this, NewContractActivity::class.java)
            startActivity(intent)

        }



        existing_contracts_btn.setOnClickListener {

            val intent = Intent(this, CaptureSignaturesActivity::class.java)
            startActivity(intent)

        }
    }


    //=========CHECKED IF USER IS LOGGED IN ========
    private fun verifyUserIsLoggedIn() {
        //===check if user is logged in ====

        val uid = FirebaseAuth.getInstance().uid


        if (uid == null) {
            //====user not logged in, redirect to register ====
            val intent = Intent(this, MainActivity::class.java)
            //clear all activities on the stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }

    }


    fun finishMyActivity() {
//        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            backToast.cancel()

            intent = Intent(Intent.ACTION_MAIN)

            intent.addCategory(Intent.CATEGORY_HOME)



            startActivity(intent)

            super.onBackPressed()

            return

        }


        this.doubleBackToExitPressedOnce = true

        backToast =  Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT)
        backToast.show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)


        finishMyActivity()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out -> {

                AlertDialog.Builder(this)
                        .setTitle("LOGOUT")
                        .setMessage("Are you sure you want to logout now?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES"
                        ) { arg0, arg1 ->
                            logUserOut()
                        }.create().show()



            }
        }
        return super.onOptionsItemSelected(item)
    }

    //====option menu=====
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //inflate the menu resource
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun logUserOut() {

        lifecycleScope.launch {
            userPreferences.saveIsLoggedIn(false)
        }


        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        //clear all activities on the stack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }


}

