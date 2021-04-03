package com.example.commanderscontracts.contracts

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.commanderscontracts.CaptureSignaturesActivity
import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.User
import com.example.commanderscontracts.models.UserContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_contract.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewContractActivity : AppCompatActivity() {

    private var mProgressBar: ProgressDialog? = null
    private var userContract: UserContract? = null
    private var currentUserId: String? = null

    private var profileLogoUri: String? = null





    //====current user logged in-===
    companion object {
        var currentUser: User? = null
        val TAG = "NewContract"
        val USER_KEY = "USER_KEY"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contract)

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)

        mProgressBar = ProgressDialog(this)

        fetchRequiredData()




        //====Set title
        supportActionBar?.title = "NEW CONTRACT"


        fetchCurrentUser()



        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy")
        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy", Date())

        create_new_contract_btn_activity.setOnClickListener {

            //performCreateNewContract()

            val reference = FirebaseDatabase.getInstance().getReference("/user-contracts/$currentUserId").push()


            val clientName = inputClientName.text.toString().trim()
            val clientAddress = inputClientAddress.text.toString().trim()
            val clientDate = inputDate.text.toString().trim()
            val clientDescription = inputDescription.text.toString().trim()
            val clientPrice = inputClientPrice.text.toString().trim()


            if (clientName.isEmpty()){
                inputClientName.error = "Name Required."
                inputClientName.requestFocus()
                return@setOnClickListener
            }


            if (clientAddress.isEmpty()){
                inputClientAddress.error = "Address Required."
                inputClientAddress.requestFocus()
                return@setOnClickListener
            }

            if (clientDate.isEmpty()){
                inputDate.error = "Date Required."
                inputDate.requestFocus()
                return@setOnClickListener
            }

            if (clientDescription.isEmpty()){
                inputDescription.error = "Description Required."
                inputDescription.requestFocus()
                return@setOnClickListener
            }


            if (clientPrice.isEmpty()){
                inputClientPrice.error = "Price Required."
                inputClientPrice.requestFocus()
                return@setOnClickListener
            }

            userContract = UserContract("",clientName,clientAddress,clientDate,clientDescription, "","","","",clientPrice)

            Log.d("NewContract"," User Contract: ${userContract}")
            val intent = Intent(this, CaptureSignaturesActivity::class.java)
            intent.putExtra(USER_KEY, userContract)
            startActivity(intent)



        }


    }


    private fun fetchRequiredData() {

      //  val reference = FirebaseDatabase.getInstance().getReference("/user-contracts/$currentUserId").push()
        currentUserId  = FirebaseAuth.getInstance().uid ?: return
        profileLogoUri = currentUser?.companyLogoImageUrl ?: return

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


         currentUserId  = FirebaseAuth.getInstance().uid ?: return

        val profileLogoUri = currentUser?.companyLogoImageUrl ?: return

        //1. get firebase reference
        val reference = FirebaseDatabase.getInstance().getReference("/user-contracts/$currentUserId").push() //to push will generate automatic node for us in rtd

        val userContract = UserContract(reference.key!!,clientName,clientAddress,clientDate,clientDescription,currentUserId!!,profileLogoUri,clientPrice,"","")

        //2. Access the reference and set some value

        reference.setValue(userContract)
            .addOnSuccessListener {

                //=====hide progress bar===
                mProgressBar!!.hide()

                Log.d(TAG, "Contract Saved Successfully: ${reference.key}")

                //createPdf( inputClientName.text.toString())

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





    fun createPdf(textToPdf: String) {

        // create a new document
        val document = PdfDocument()

        // crate a page description
        var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()

        // start a page
        var page: PdfDocument.Page = document.startPage(pageInfo)
        var canvas = page.canvas
        var paint = Paint()
        var title = Paint()

        paint.color = Color.RED
        canvas.drawCircle(50F, 50F, 30F, paint)
        paint.color = Color.BLACK
        canvas.drawText(textToPdf, 80F, 50F, paint)

        title.setTextAlign(Paint.Align.CENTER)
        //canvas.drawt
        // finish the page
        document.finishPage(page)
        // draw text on the graphics object of the page

        // Create Page 2
        pageInfo = PdfDocument.PageInfo.Builder(300, 600, 2).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        paint = Paint()
        paint.color = Color.BLUE
        canvas.drawCircle(100F, 100F, 100F, paint)
        document.finishPage(page)

        // write the document content
        val directory_path = Environment.getExternalStorageDirectory().path + "/mypdf/"
        val file = File(directory_path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val targetPdf = directory_path + "test-2.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error " + e.toString())
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        //isPrinting = false
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


