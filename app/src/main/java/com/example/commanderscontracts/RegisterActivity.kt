package com.example.commanderscontracts

import android.app.Activity
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null  //global variable, because we need to access it. Location where the image is stored on the device

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




        new_contract_btn.setOnClickListener {
            performRegister()

        }



    }



    private fun performRegister() {
        val name = inputCompanyName.text.toString().trim()
        val address = inputCompanyAddress.text.toString().trim()
        val phone = inputCompanyPhoneNumber.text.toString().trim()
        val email = inputCompanyEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()
        val confirmPassword = inputConfirmPassword.text.toString().trim()

        if (name.isEmpty()){
            inputCompanyName.error = "Company Name Required."
            inputCompanyName.requestFocus()
            return

        }

        if (address.isEmpty()){
            inputCompanyAddress.error = "Company Address Required."
            inputCompanyAddress.requestFocus()
            return

        }

        if (phone.isEmpty()){
            inputCompanyPhoneNumber.error = "Company Phone Required."
            inputCompanyPhoneNumber.requestFocus()
            return

        }


        if (email.isEmpty()){
            inputCompanyEmail.error = "Company E-mail Required."
            inputCompanyEmail.requestFocus()
            return

        }

        if (!isValidEmail(email)) {
            // Toast.makeText(this, "Email address is not valid", Toast.LENGTH_SHORT).show()
            inputCompanyEmail.error = "Email address is not valid"
            inputCompanyEmail.requestFocus()
            inputCompanyEmail.isEnabled = true
            return

        }


        if (password.isEmpty()){
            inputPassword.error = "Password Required."
            inputPassword.requestFocus()
            return

        }


        if (confirmPassword.isEmpty()){
            inputConfirmPassword.error = "Confirm Password Required."
            inputConfirmPassword.requestFocus()
            return

        }


        if (password.length < 8) {
            inputPassword.error = "Password should be atleast 8 characters"
            inputPassword.requestFocus()
            inputPassword.isEnabled = true

            return

        }


        if (confirmPassword.length < 8) {
            inputConfirmPassword.error = "Password should be atleast 8 characters"
            inputConfirmPassword.requestFocus()
            inputConfirmPassword.isEnabled = true

            return

        }


        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            if(!password.equals(confirmPassword)) {
                inputConfirmPassword.error = "The two passwords do not match"
                inputConfirmPassword.requestFocus()
                inputConfirmPassword.isEnabled = true
                return

            } else {
                //Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }




        //========firebase auth to perform create user with email and password====
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successfull
                Log.d("RegisterActivity", "successfully created user with the UId: ${it.result?.user?.uid}")


                //Then we store image to firebase
                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user ${it.message}")
                Toast.makeText(this, "Failed to create user ${it.message}", Toast.LENGTH_LONG).show()
            }


    }




    //==============we capture the result from photo selector==========
//=========the method is called when the intent of photo selector is finished======
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Do a couple of checks

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //1. we proceed and check what selected image was ---
            Log.d("RegisterActivity", "photo was selected")

            //2. we have to figure which photo it is inside out app, the pass data has data, uri will represent the location of where the imahe is stored in the device

             selectedPhotoUri = data.data
            //3. we can use the uri to get access to the image as a bitmap

            val bitmap =  MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            //4. pass it inside the button
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_register_button.setBackgroundDrawable(bitmapDrawable)


            select_photo_imageview_register.setImageBitmap(bitmap)

            //set phot nutton alpho to 0 to prevent hiding
            select_photo_register_button.alpha = 0f






        }


    }






    //fun to upload image to firebase
    private fun uploadImageToFirebaseStorage() {

        //==check selected photo uri
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString() //random string
        val ref =  FirebaseStorage.getInstance().getReference("/images/$filename") //save inside images folder


        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                //=====After finishing loading the image, we need to have access to file location(download loaction)

                ref.downloadUrl.addOnSuccessListener {
                    //===file location===
                    Log.d("RegisterActivity", "File location: $it")


                    saveUserToFirebaseDataBase(it.toString())


                    //=====LAUNCH ACTIVITY

                    val intent = Intent(this, NewOrExistingContracts::class.java)

                    //clear all activities on the stack
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }

            }

            .addOnFailureListener{
                //

                Log.d("RegisterActivity", "Failed to save")

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





//=================SAVE USER DATE TO DATABASE==========
    private  fun saveUserToFirebaseDataBase(companyLogoImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid?:""

        val ref =   FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, inputCompanyName.text.toString(), inputCompanyAddress.text.toString(), inputCompanyPhoneNumber.text.toString(),inputCompanyEmail.text.toString(), companyLogoImageUrl)


        ref.setValue(user).
        addOnSuccessListener {

            Log.d("RegisterActivity", "We saved the user to firebase database")


        }

    }





    ///===Class User ===
    class User(val uid: String, val companyName:String, val companyAddress:String, val companyPhone:String,val companyEmail:String, val companyLogoImageUrl:String)





}