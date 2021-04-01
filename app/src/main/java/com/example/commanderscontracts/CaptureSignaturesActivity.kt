package com.example.commanderscontracts

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_capture_signatures.*
import kotlinx.android.synthetic.main.activity_signature.*
import java.io.ByteArrayOutputStream

class CaptureSignaturesActivity : AppCompatActivity(),OnSignedCaptureListener {
    var isUserOrContractor: Int? = null
    var clientFileName: String? = null
    var contractorFileName:String? =  null
    var clientSignUri: Uri?  = null
    var contractorSignUri: Uri?  = null

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



        submit_signature_btn.setOnClickListener{
            uploadImage()
        }


    }



    private fun uploadImage() {

    }

    //submit_signature_btn

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        if(isUserOrContractor ==  WhichButton.CLIENT_SIGN.ordinal ){
            Log.d("CaptureSignature", "Client Bitmap: ${bitmap} ======= Client fileUri ${fileUri}")

           // clientSignUri =  getImageUriFromBitmap(this,bitmap)
            clientFileName = fileUri



            Log.d("CaptureSignature", " Client BitmapUri ${clientSignUri}")

            Log.d("CaptureSignature", " Client fileUri ${clientFileName}")

            client_image_view.setImageBitmap(bitmap)

        } else {

            contractor_image_view.setImageBitmap(bitmap)

            Log.d("CaptureSignature", "Contractor Bitmap: ${bitmap} ======= Contractor fileUri ${fileUri}")

           // contractorSignUri = getImageUriFromBitmap(this,bitmap)
            contractorFileName = fileUri

            Log.d("CaptureSignature", " Contractor BitmapUri ${contractorSignUri}")

            Log.d("CaptureSignature", " Client fileUri ${contractorFileName}")

        }





    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }


    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }
}