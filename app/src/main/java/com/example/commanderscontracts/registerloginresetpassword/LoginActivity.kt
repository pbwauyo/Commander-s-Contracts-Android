package com.example.commanderscontracts.registerloginresetpassword

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.commanderscontracts.R
import com.example.commanderscontracts.UserPreferences
import com.example.commanderscontracts.ViewModel.MainViewModel
import com.example.commanderscontracts.contracts.NewOrExistingContracts
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var userPreferences: UserPreferences
    private var mProgressBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)

        userPreferences = UserPreferences(this)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mProgressBar = ProgressDialog(this)



//        val userPref = UserPreferences(this)
//
//        userPref.isLoggedIn.asLiveData().observe(this, Observer{
//
//
//            Toast.makeText(this,"Token is ${it}", Toast.LENGTH_LONG).show()
//
//            if(it == true) {
//
//                val intent = Intent(this, NewOrExistingContracts::class.java)
////               //clear all activities on the stack
//               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//               startActivity(intent)
//
//            } else {
//
//            }
//
//        })


//        viewModel.readFromDataStore.observe(this,{
//                isLoggedIn ->
//
//           if (isLoggedIn == true) {
//
//               val intent = Intent(this, NewOrExistingContracts::class.java)
//               //clear all activities on the stack
//               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//               startActivity(intent)
//
//           } else {
//
//               val intent = Intent(this, LoginActivity::class.java)
//               //clear all activities on the stack
//               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//               startActivity(intent)
//
//           }
//
//
//        })
//




        btnLogin.setOnClickListener {
            performLogin()

        }

        resend_verification_email.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view:View) {
                if (FirebaseAuth.getInstance().currentUser != null)
                {
                    FirebaseAuth.getInstance().currentUser.reload()
                    if (!FirebaseAuth.getInstance().currentUser.isEmailVerified)
                    {
//                        resend_verification_email.visibility = View.VISIBLE
//                        dont_have_verification_email.visibility = View.VISIBLE
                        FirebaseAuth.getInstance().currentUser.sendEmailVerification()
                        Toast.makeText(this@LoginActivity, "Verification Email Resent!, please check your email inbox..", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        resend_verification_email.visibility = View.INVISIBLE
                        dont_have_verification_email.visibility = View.INVISIBLE
                        Toast.makeText(this@LoginActivity, "Your email has been verified! You can login now.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })


        backToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_out_left, R.anim
        }


        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }





    }



    //=============VERIFY USER ===============

    fun verifyUser() {
        val mAuth =  FirebaseAuth.getInstance()
        if ( mAuth!!.currentUser == null) {
            //Manage error
            return
        }

        mAuth!!.currentUser!!.reload()
                .addOnCompleteListener { reloadTask ->
                    //After the reload the currentUser can be null because it takes some time in be updated
                    if (reloadTask.isSuccessful && mAuth!!.currentUser != null) {
                        val user = mAuth!!.currentUser!!
                        user.reload()

                        val verified = user.isEmailVerified
                        //Check our verified param and continue

                        if (verified) {




                            updateUI()







//                            viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//                            viewModel.saveToDataStore(true)


                        } else {

                            Toast.makeText(this@LoginActivity, "Please check your email inbox and verify your email to continue to login.",
                                    Toast.LENGTH_LONG).show()

//                            resend_verification_email.visibility = View.VISIBLE
//                            dont_have_verification_email.visibility = View.VISIBLE

                        }

                    } else {


                        Toast.makeText(this@LoginActivity, "Please check your email inbox and verify your email to continue to login.",
                                Toast.LENGTH_SHORT).show()

                    } //Manage error
                }
    }


    //===========Update UI====

    private fun updateUI() {


        lifecycleScope.launch {
            userPreferences.saveIsLoggedIn(true)

        }

        val intent = Intent(this, NewOrExistingContracts::class.java)
        //clear all activities on the stack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }



//===========LOGIN FUN=======
    private fun performLogin() {
        val email = inputEmail.text.toString().trim()
        val password = inputPasswordLogin.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {

            // checks for email and password text field to make sure they are not empty
            Toast.makeText(this, "Please Enter Email Address and Password", Toast.LENGTH_LONG).show()
            return

        }





        mProgressBar!!.setMessage("Logging in user...")
        mProgressBar!!.show()


        //========firebase auth====
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {




                if (!it.isSuccessful) return@addOnCompleteListener
                //======else===
                Log.d("Login", "successfully created user with the UId: ${it.result?.user?.uid}")

                mProgressBar!!.hide()

                //=======clear text field ====

                inputEmail.text.clear()
                inputPasswordLogin.text.clear()


                //=====LAUNCH ACTIVITY

                verifyUser()


            }
            .addOnFailureListener {
                Log.d("Login", "Failed to Login ${it.message}")
            }

    }











    fun ShowHidePass(view:View) {
        if (view.getId() === R.id.show_pass_btn)
        {
            if (inputPasswordLogin.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_off_black)
                //Show Password
                inputPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            }
            else
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_black)
                //Hide Password
                inputPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }

            // cursor reset his position so we need set position to the end of text
            inputPasswordLogin.setSelection(inputPasswordLogin.getText().length)
        }
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }
}