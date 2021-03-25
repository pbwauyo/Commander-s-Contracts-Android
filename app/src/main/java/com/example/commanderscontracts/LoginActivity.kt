package com.example.commanderscontracts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)



        btnLogin.setOnClickListener {

            performLogin()

        }


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




    private fun performLogin() {
        val email = inputEmail.text.toString().trim()
        val password = inputPasswordLogin.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {

            // checks for email and password text field to make sure they are not empty
            Toast.makeText(this, "Please Enter Email Address and Password", Toast.LENGTH_LONG).show()
            return

        }


        //========firebase auth====
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //======else===
                Log.d("Login", "successfully created user with the UId: ${it.result?.user?.uid}")
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