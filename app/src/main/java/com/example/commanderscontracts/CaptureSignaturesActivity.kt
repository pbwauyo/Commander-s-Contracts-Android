package com.example.commanderscontracts

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_capture_signatures.*
import kotlinx.android.synthetic.main.activity_signature.*

class CaptureSignaturesActivity : AppCompatActivity(),OnSignedCaptureListener {
    var isUserOrContractor: Int? = null

    enum class WhichButton {
        CLIENT_SIGN,
        CONTRACTOR_SIGN,
        DEFAULT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_signatures)

        isUserOrContractor = WhichButton.DEFAULT.ordinal

        //====Set title
        supportActionBar?.title = "CAPTURE SIGNATURES"


            contractor_signature_button.setOnClickListener {
                isUserOrContractor = WhichButton.CONTRACTOR_SIGN.ordinal
                showDialog()
            }

            client_signature_button.setOnClickListener {
                showDialog()

                isUserOrContractor = WhichButton.CLIENT_SIGN.ordinal
            }


    }

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        if(isUserOrContractor ==  WhichButton.CLIENT_SIGN.ordinal ){

            client_image_view.setImageBitmap(bitmap)

        } else {

            contractor_image_view.setImageBitmap(bitmap)

        }





    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }
}