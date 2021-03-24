package com.example.commanderscontracts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)


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



//    fun ShowHidePassword(view: View) {
//
//        if(view.getId()==R.id.show_pass_btn){
//            if(inputPasswordLogin.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
//
//                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_off_black);
//
//                //Show Password
//
//            }
//        }
//
//    }


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