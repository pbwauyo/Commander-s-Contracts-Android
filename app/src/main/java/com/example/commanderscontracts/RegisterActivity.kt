package com.example.commanderscontracts

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)

        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }




    }









fun back(view: View?) {
        finishMyActivity()
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }





    fun ShowHidePass(view:View) {
        if (view.id === R.id.show_pass_btn_one)
        {
            if (inputPassword.transformationMethod.equals(PasswordTransformationMethod.getInstance()))
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_off_black)
                //Show Password
//

                inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_black)
                //Hide Password
                inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            // cursor reset his position so we need set position to the end of text
            inputPassword.setSelection(inputPassword.getText().length)
        }
    }



    fun ShowHidePassTwo(view:View) {
        if (view.id === R.id.show_pass_btn_two)
        {
            if (inputConfirmPassword.transformationMethod.equals(PasswordTransformationMethod.getInstance()))
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_off_black)
                //Show Password
                inputConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else
            {
                ((view) as ImageView).setImageResource(R.drawable.ic_visibility_black)
                //Hide Password
                inputConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            // cursor reset his position so we need set position to the end of text
            inputConfirmPassword.setSelection(inputConfirmPassword.text.length)
        }
    }

}