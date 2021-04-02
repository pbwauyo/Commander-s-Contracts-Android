package com.example.commanderscontracts

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.commanderscontracts.contracts.ExistingContractsActivity
import com.example.commanderscontracts.contracts.NewContractActivity
import com.example.commanderscontracts.contracts.NewOrExistingContracts
import com.example.commanderscontracts.models.UserContract
import com.example.commanderscontracts.registerloginresetpassword.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_capture_signatures.*
import kotlinx.android.synthetic.main.activity_signature.*
import java.io.ByteArrayOutputStream
import java.util.*


class CaptureSignaturesActivity : AppCompatActivity(),OnSignedCaptureListener {
    private var mProgressBar: ProgressDialog? = null
    var isUserOrContractor: Int? = null
    var clientFileName: String? = null
    var contractorFileName:String? =  null
    private var clientSignUri: Uri?  = null
    var clientBitMap: Bitmap?  = null
    private var contractorSignUri: Uri?  = null



    enum class WhichButton {
        CLIENT_SIGN,
        CONTRACTOR_SIGN,
        DEFAULT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_signatures)

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {

        }



        mProgressBar = ProgressDialog(this)

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

           // if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
                // do your stuff..
                Log.d("UploadBtn", "Submit Btn tapped")
                Toast.makeText(this, "Submit Sign tapped", Toast.LENGTH_LONG).show()
                uploadImage()
          //  }


        }




    }













    private fun uploadImage() {

        if(clientSignUri == null) return

       // val filename = UUID.randomUUID().toString() //random string
        val ref =  FirebaseStorage.getInstance().getReference("/signatures/$contractorFileName") //save inside images folder

        ref.putFile(clientSignUri!!).addOnSuccessListener{ it ->
            Log.d("CaptureActivity", "Successfully uploaded image: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                //===file location===
                Log.d("CaptureActivity", "File location: ${it}")

                var imageLink = it.toString()


               // saveContractsToDB(clientSignUri!!)

                saveContractsToDB(imageLink)







                //=====LAUNCH ACTIVITY

                val intent = Intent(this, NewOrExistingContracts::class.java)

                //clear all activities on the stack
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }


            .addOnFailureListener{
                //

                Log.d("CaptureActivity", "Failed to save")

            }











    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }




    private fun saveContractsToDB(clientSignImageUri: String) {

        mProgressBar!!.setMessage("Saving Contract...")
        mProgressBar!!.show()


        val currentUserId  = FirebaseAuth.getInstance().uid ?: return

        val profileLogoUri = NewContractActivity.currentUser?.companyLogoImageUrl ?: return



        //1. get firebase reference
        val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").push() //to push will generate automatic node for us in rtd

        val userContract = UserContract(
            reference.key!!,
            "clientName",
            "clientAddress",
            "clientDate",
            "clientDescription",
            currentUserId,
            profileLogoUri,
                clientSignImageUri,

                "200"

        )

        reference.setValue(userContract)
            .addOnSuccessListener {

                Log.d("CaptureActivity", "User contracts ${userContract.clientSignUri}")

                //=====hide progress bar===
                mProgressBar!!.hide()

                Log.d("CaptureActivity", "Contract Saved Successfully: ${reference.key}")


            }



    }

    //submit_signature_btn

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        if(isUserOrContractor ==  WhichButton.CLIENT_SIGN.ordinal ){
            Log.d("CaptureSignature", "CLIENT BITMAP: ${bitmap} ")

         // clientSignUri = getImageUriFromBitmap(this,bitmap)

            clientSignUri = getImageUriFromBitmap(this, bitmap)

            clientBitMap = bitmap
            clientFileName = fileUri



            Log.d("CaptureSignature", " Client BitmapUri ${clientSignUri}")

            Log.d("CaptureSignature", " Client fileUri ${clientFileName}")

            client_image_view.setImageBitmap(bitmap)

        } else {

            contractor_image_view.setImageBitmap(bitmap)

            Log.d("CaptureSignature", "CONTRACTOR  BITMAP: ${bitmap} ")

            contractorSignUri = getImageUriFromBitmap(this,bitmap)
            contractorFileName = fileUri

            Log.d("CaptureSignature", " Contractor BitmapUri ${contractorSignUri}")

            Log.d("CaptureSignature", " Client fileUri ${contractorFileName}")

        }





    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }



    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path.toString())
    }


//    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
//        val bytes = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
//        return Uri.parse(path.toString())
//    }




    //https://stackoverflow.com/questions/37672338/java-lang-securityexception-permission-denial-reading-com-android-providers-me/37672627
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    private fun checkPermissionREAD_EXTERNAL_STORAGE(
        context: Context?
    ): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    showDialog(
                        "External Storage", context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } else {
                    ActivityCompat
                        .requestPermissions(
                            (context as Activity?)!!,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }


    fun showDialog(
        msg: String, context: Context,
        permission: String
    ) {
        val alertBuilder: AlertDialog.Builder =  AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission Necessary")
        alertBuilder.setMessage("$msg permission is necessary to enable the App capture signatures")
        alertBuilder.setPositiveButton(android.R.string.yes,
            DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    (context as Activity?)!!, arrayOf(permission),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            })
        val alert: AlertDialog = alertBuilder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //1. we proceed and check what selected image was ---
            Log.d("CaptureSignature", "photo was selected")

            //2. we have to figure which photo it is inside out app, the pass data has data, uri will represent the location of where the image is stored in the device
            clientSignUri = data.data

            val bitmap =  MediaStore.Images.Media.getBitmap(contentResolver, clientSignUri)



        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do your stuff
            } else {
                Toast.makeText(
                    this@CaptureSignaturesActivity, "GET_ACCOUNTS Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> super.onRequestPermissionsResult(
                requestCode, permissions!!,
                grantResults
            )
        }
    }
}