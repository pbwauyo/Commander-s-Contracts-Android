package com.example.commanderscontracts.contracts

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.User
import com.example.commanderscontracts.models.UserContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_contract.*
import java.text.SimpleDateFormat
import java.util.*

class NewContractActivity : AppCompatActivity() {

    private var mProgressBar: ProgressDialog? = null


    //====current user logged in-===
    companion object {
        var currentUser: User? = null
        val TAG = "NewContract"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contract)

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)

        mProgressBar = ProgressDialog(this)

        //====Set title
        supportActionBar?.title = "NEW CONTRACT"


        fetchCurrentUser()



        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy")
        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy", Date())

        create_new_contract_btn_activity.setOnClickListener {

            performCreateNewContract()



        }


    }

    private fun performCreateNewContract() {
        val clientName = inputClientName.text.toString().trim()
        val clientAddress = inputClientAddress.text.toString().trim()
        val clientDate = inputDate.text.toString().trim()
        val clientDescription = inputDescription.text.toString().trim()
        val clientPrice = inputClientPrice.text.toString().trim()


        if (clientName.isEmpty()){
            inputClientName.error = "Name Required."
            inputClientName.requestFocus()
            return
        }


        if (clientAddress.isEmpty()){
            inputClientAddress.error = "Address Required."
            inputClientAddress.requestFocus()
            return
        }

        if (clientDate.isEmpty()){
            inputDate.error = "Date Required."
            inputDate.requestFocus()
            return
        }

        if (clientDescription.isEmpty()){
            inputDescription.error = "Description Required."
            inputDescription.requestFocus()
            return
        }


        if (clientPrice.isEmpty()){
            inputClientPrice.error = "Price Required."
            inputClientPrice.requestFocus()
            return
        }


        mProgressBar!!.setMessage("Saving Contract...")
        mProgressBar!!.show()


        val currentUserId  = FirebaseAuth.getInstance().uid ?: return

        val profileLogoUri = currentUser?.companyLogoImageUrl ?: return

        //1. get firebase reference
        val reference = FirebaseDatabase.getInstance().getReference("/user-contracts/$currentUserId").push() //to push will generate automatic node for us in rtd

        val userContract = UserContract(reference.key!!,clientName,clientAddress,clientDate,clientDescription,currentUserId,profileLogoUri,clientPrice)

        //2. Access the reference and set some value

        reference.setValue(userContract)
            .addOnSuccessListener {

                //=====hide progress bar===
                mProgressBar!!.hide()

                Log.d(TAG, "Contract Saved Successfully: ${reference.key}")

                val intent = Intent(this, NewOrExistingContracts::class.java)
                startActivity(intent)

                //-=====clear the text after send tapped===

                inputClientName.text.clear()
                inputClientAddress.text.clear()
                inputDate.text.clear()
                inputDescription.text.clear()
                inputClientPrice.text.clear()






        }









    }


    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("NewContract","Current User: ${currentUser?.companyName}")

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }





    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }



    //======Date picker ======
    //https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat(format, Locale.US)
                    setText(sdf.format(myCalendar.time))
                }

        setOnClickListener {
            DatePickerDialog(
                    context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }






}


