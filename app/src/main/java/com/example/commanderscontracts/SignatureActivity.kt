package com.example.commanderscontracts

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_signature.*

class SignatureActivity : AppCompatActivity(),OnSignedCaptureListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        buttonShowDialog.setOnClickListener { showDialog() }
    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }
    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {
        imageView.setImageBitmap(bitmap)
    }
}