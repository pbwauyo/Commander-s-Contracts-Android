package com.example.commanderscontracts.contracts

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.commanderscontracts.R
import kotlinx.android.synthetic.main.activity_new_contract.*
import java.text.SimpleDateFormat
import java.util.*

class NewContractActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contract)

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right)

        //====Set title
        supportActionBar?.title = "NEW CONTRACT"



        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy")
        inputDate.transformIntoDatePicker(this, "MM/dd/yyyy", Date())

        create_new_contract_btn_activity.setOnClickListener {



        }


        


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


