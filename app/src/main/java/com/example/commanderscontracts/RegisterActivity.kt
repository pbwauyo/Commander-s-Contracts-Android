package com.example.commanderscontracts

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null  //global variable, because we need to access it

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        overridePendingTransition(R.anim.go_in, R.anim.go_out)

        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        select_photo_register_button.setOnClickListener {

            Log.d("RegisterActivity", "try to show photo selector")

            //======start photo selector

            //intent for photo selector
            val intent = Intent(Intent.ACTION_PICK)

            //specifying the type
            intent.type = ("image/*")


            //start activity for result, for the result that comes and request code, we gonna use 0
            startActivityForResult(intent, 0)

        }












        btnRegister.setOnClickListener {

            val name = inputCompanyName.text.toString().trim()
            val address = inputCompanyAddress.text.toString().trim()
            val phone = inputCompanyPhoneNumber.text.toString().trim()
            val email = inputCompanyEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            if (name.isEmpty()){
                inputCompanyName.error = "Company Name Required."
                inputCompanyName.requestFocus()
                return@setOnClickListener

            }

            if (address.isEmpty()){
                inputCompanyAddress.error = "Company Address Required."
                inputCompanyAddress.requestFocus()
                return@setOnClickListener

            }

            if (phone.isEmpty()){
                inputCompanyPhoneNumber.error = "Company Phone Required."
                inputCompanyPhoneNumber.requestFocus()
                return@setOnClickListener

            }


            if (email.isEmpty()){
                inputCompanyEmail.error = "Company E-mail Required."
                inputCompanyEmail.requestFocus()
                return@setOnClickListener

            }

            if (!isValidEmail(email)) {
                // Toast.makeText(this, "Email address is not valid", Toast.LENGTH_SHORT).show()
                inputCompanyEmail.error = "Email address is not valid"
                inputCompanyEmail.requestFocus()
                inputCompanyEmail.isEnabled = true
                return@setOnClickListener

            }


            if (password.isEmpty()){
                inputPassword.error = "Password Required."
                inputPassword.requestFocus()
                return@setOnClickListener

            }


            if (confirmPassword.isEmpty()){
                inputConfirmPassword.error = "Confirm Password Required."
                inputConfirmPassword.requestFocus()
                return@setOnClickListener

            }


            if (password.length < 8) {
                inputPassword.error = "Password should be atleast 8 characters"
                inputPassword.requestFocus()
                inputPassword.isEnabled = true

                return@setOnClickListener

            }


            if (confirmPassword.length < 8) {
                inputConfirmPassword.error = "Password should be atleast 8 characters"
                inputConfirmPassword.requestFocus()
                inputConfirmPassword.isEnabled = true

                return@setOnClickListener

            }


            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                if(!password.equals(confirmPassword)) {
                    inputConfirmPassword.error = "The two passwords do not match"
                    inputConfirmPassword.requestFocus()
                    inputConfirmPassword.isEnabled = true
                    return@setOnClickListener

                } else {
                    //Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
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




fun back(view: View?) {
        finishMyActivity()
    }


    private fun finishMyActivity(){
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




    //we capture the result from photo selector
//the method is called when the intent of photo selector is finished
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Do a couple of checks

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //1. we proceed and check what selected image was ---
            Log.d("RegisterActivity", "photo was selected")

            //2. we have to figure which photo it is inside out app, the pass data has data, uri will represent the location of where the imahe is stored in the device

            val selectedPhotoUri = data.data
            //3. we can use the uri to get access to the image as a bitmap

            val bitmap =  MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            //4. pace it inside the button
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_register_button.setBackgroundDrawable(bitmapDrawable)



        }


    }

}