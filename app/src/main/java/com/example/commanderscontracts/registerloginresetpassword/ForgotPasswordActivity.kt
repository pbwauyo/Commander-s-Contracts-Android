package com.example.commanderscontracts.registerloginresetpassword

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_forgot_password.*



class ForgotPasswordActivity : AppCompatActivity() {

    private var mProgressBar: ProgressDialog? = null

    //====current user logged in-===
    companion object {
        var currentUser: User? = null
        val TAG = "ForgotPasswordActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)

        mProgressBar = ProgressDialog(this)


        fetchCurrentUser()
        verifyUserIsLoggedIn()



        btnResetPassword.setOnClickListener {

//            val emailAddress = inputEmail.text.toString().trim()
//
//            if (emailAddress.isEmpty()){
//                inputEmail.error = "E-mail Required."
//                inputEmail.requestFocus()
//                return@setOnClickListener
//
//            }




//            if (!isValidEmail(emailAddress)) {
//
//                // Toast.makeText(this, "Email address is not valid", Toast.LENGTH_SHORT).show()
//                inputEmail!!.error = "Email address is not valid"
//                inputEmail!!.requestFocus()
//                inputEmail!!.isEnabled = true
//                return@setOnClickListener
//
//            }
//
//            if(currentUser == null) return@setOnClickListener

             resetPassword()
           // resetUserPassword(emailAddress)
        }
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }



    private fun resetPassword() {

        val emailAddress = inputEmail.text.toString().trim()

        if (emailAddress.isEmpty()){
            inputEmail.error = "E-mail Required."
            inputEmail.requestFocus()
            return

        }


        if (!isValidEmail(emailAddress)) {

            // Toast.makeText(this, "Email address is not valid", Toast.LENGTH_SHORT).show()
            inputEmail!!.error = "Email address is not valid"
            inputEmail!!.requestFocus()
            inputEmail!!.isEnabled = true
            return

        }


        mProgressBar!!.setMessage("Sending password reset link, please wait...")
        mProgressBar!!.show()



        if (!TextUtils.isEmpty(emailAddress)) {
            FirebaseAuth.getInstance()!!
                    .sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->



                        if (task.isSuccessful) {

                            mProgressBar!!.hide()
                            val message = "Password reset link sent, please  check your Email inbox"
                            Log.d(TAG, message)

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                            updateUI()

                        } else {
                            Log.w(TAG, task.exception!!.message)
                            Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }







    }





    private fun resetUserPassword(email:String) {
        val mAuth = FirebaseAuth.getInstance()
        val progressDialog = ProgressDialog(this@ForgotPasswordActivity)
        progressDialog.setMessage("Verifying..")
        progressDialog.show()


        mAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                // Do something when successful

                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Reset password instructions has been sent to your email",
                        Toast.LENGTH_SHORT).show()

                updateUI()

            } else {
                progressDialog.dismiss()

                Log.w(TAG, task.exception!!.message)
                Toast.makeText(applicationContext,
                        "Email don't exist", Toast.LENGTH_SHORT).show()

            }
        }


    }



    //======Check if email is valid=====
    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }



    private fun updateUI() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

        finish()
    }




    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("ForgotPasswordActivity","Current User: ${currentUser?.companyName}")

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }





    //=========CHECKED IF USER IS LOGGED IN ========
    private fun verifyUserIsLoggedIn() {
        //===check if user is logged in ====

        val uid = FirebaseAuth.getInstance().uid


        if(uid == null) {
            //====user not logged in, redirect to register ====
            val intent = Intent(this, RegisterActivity::class.java)
            //clear all activities on the stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }

    }
}