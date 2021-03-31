package com.example.commanderscontracts

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_capture_signatures.*
import kotlinx.android.synthetic.main.activity_signature.*

class CaptureSignaturesActivity : AppCompatActivity(),OnSignedCaptureListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_signatures)

        //====Set title
        supportActionBar?.title = "CAPTURE SIGNATURES"


            contractor_signature_button.setOnClickListener { showDialog() }

            client_signature_button.setOnClickListener { showDialog() }


    }

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        client_image_view.setImageBitmap(bitmap)
        contractor_image_view.setImageBitmap(bitmap)

    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }
}