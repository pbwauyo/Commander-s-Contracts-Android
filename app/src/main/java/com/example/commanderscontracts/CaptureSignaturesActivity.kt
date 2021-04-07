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
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.commanderscontracts.contracts.ExistingContractsActivity
import com.example.commanderscontracts.contracts.NewContractActivity
import com.example.commanderscontracts.contracts.NewContractActivity.Companion.USER_KEY
import com.example.commanderscontracts.models.UserContract
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_capture_signatures.*
import kotlinx.android.synthetic.main.activity_signature.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap


class CaptureSignaturesActivity : AppCompatActivity(),OnSignedCaptureListener {
    private var mProgressBar: ProgressDialog? = null
    var isUserOrContractor: Int? = null
    var clientFileName: String? = null
    var contractorFileName:String? =  null
    private var clientSignUri: Uri?  = null
    var clientBitMap: Bitmap?  = null
    private var contractorSignUri: Uri?  = null

    var userReturedContracts: UserContract? = null

    var usersRef:DatabaseReference? = null
    var firebaseUser:FirebaseUser? = null
    private var storageRef:StorageReference? = null
    private var coverChecker: String? = ""

    private var clientSignUrl: String? = null





    enum class WhichButton {
        CLIENT_SIGN,
        CONTRACTOR_SIGN,
        DEFAULT
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_signatures)

        userReturedContracts = intent.getParcelableExtra<UserContract>(USER_KEY)


        val uid = FirebaseAuth.getInstance().uid?:""


        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersRef = FirebaseDatabase.getInstance().getReference("/user-contracts/$uid").push()

        //val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").push()

        storageRef = FirebaseStorage.getInstance().reference.child("signatures")

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


            Log.d("UploadBtn", "Submit Btn tapped")
            Toast.makeText(this, "Submit Sign tapped", Toast.LENGTH_LONG).show()



                 uploadUserImages()





        }




    }




//
//    private fun uploadUserImages() {
//        val progressBar = ProgressDialog(this)
//
//        progressBar.setMessage("Uploading image, Please wait...")
//        progressBar.setTitle("Image Upload")
//        progressBar.show()
//
//        val clientSignFileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
//        var clientSignUploadTask:StorageTask<*>
//        clientSignUploadTask = clientSignFileRef.putFile(clientSignUri!!)
//        clientSignUploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
//            if(!task.isSuccessful){
//                task.exception?.let {
//                    throw it
//                }
//            }
//            clientSignFileRef.downloadUrl
//        }).addOnCompleteListener{
//
//        }
//
//        val contractorSignFileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
//        var contractorSignUploadTask:StorageTask<*>
//        contractorSignUploadTask = contractorSignFileRef.putFile(contractorSignUri!!)
//        contractorSignUploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
//            if(!task.isSuccessful){
//                task.exception?.let {
//                    throw it
//                }
//            }
//            contractorSignFileRef.downloadUrl
//        }).addOnCompleteListener{
//
//        }
//
//        lifecycleScope.launch(Dispatchers.Default){
//            val clientSignUrl = clientSignUploadTask.await().toString()
//
//            val contractorSignUrl = contractorSignUploadTask.await().toString()
//
//
//            Log.d("TASK", "CONTRACTOR SIGN Url: ${contractorSignUrl}")
//            Log.d("TASK", "CLIENT SIGN Url: ${clientSignUrl}")
//
//            if(isUserOrContractor ==  WhichButton.CONTRACTOR_SIGN.ordinal ){
//                val signaturesMap = HashMap<String, Any>()
//                signaturesMap["contractorSignUri"] = contractorSignUrl
//                signaturesMap["clientSignUri"] = clientSignUrl
//                usersRef!!.updateChildren(signaturesMap)
//            }
//
//            saveContractsToDB(clientSignUrl, contractorSignUrl)
//        }
//    }






    private fun uploadUserImages() {
        val progressBar = ProgressDialog(this)

        progressBar.setMessage("Uploading image, Please wait...")
       // progressBar.setTitle("Image Upload")
        progressBar.show()

        val clientSignFileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
        val clientSignUploadTask = clientSignFileRef.putFile(clientSignUri!!).addOnFailureListener {
            //LOG ERROR HERE INCASE OF ERROR DURING UPLOAD
        }

        val clientDownloadUrlTask = clientSignUploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            clientSignFileRef.downloadUrl
        }).addOnCompleteListener { task ->

            //https://stackoverflow.com/questions/60689182/how-to-get-download-url-from-uploaded-files-using-firebase
            if (task.isSuccessful) {
               // val downloadUri = task.result

                clientSignUri = task.result

                Log.d("LINK", "DOWNLOAD LINK CLIENT: ${clientSignUri}")
            } else {

            }
        }

        val contractorSignFileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
        val contractorSignUploadTask = contractorSignFileRef.putFile(contractorSignUri!!).addOnFailureListener {
            //LOG ERROR HERE INCASE OF ERROR DURING UPLOAD
        }
        val contractorDownloadUrlTask = contractorSignUploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            contractorSignFileRef.downloadUrl

        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //val downloadUri = task.result
                    contractorSignUri = task.result

                Log.d("LINK", "DOWNLOAD LINK CONTRACTOR: ${contractorSignUri}")

            } else {

            }
        }

        lifecycleScope.launch(Dispatchers.Default){

            val clientSignUrl = clientDownloadUrlTask.await().toString()
            val contractorSignUrl = contractorDownloadUrlTask.await().toString()

            Log.d("LINK", "CLIENT: $clientSignUrl")
            Log.d("LINK", "CONTRACTOR: $contractorSignUrl")


            if(isUserOrContractor ==  WhichButton.CONTRACTOR_SIGN.ordinal ){
                val signaturesMap = HashMap<String, Any>()
                signaturesMap["contractorSignUri"] = clientSignUrl
                signaturesMap["clientSignUri"] = contractorSignUrl
                usersRef!!.updateChildren(signaturesMap)
            }

            saveContractsToDB(clientSignUrl, contractorSignUrl)
        }
    }



















    private fun saveContractsToDB(clientSignImageUri: String, contractorSignImageUri: String) {


        val currentUserId  = FirebaseAuth.getInstance().uid ?: return

        val profileLogoUri = NewContractActivity.currentUser?.companyLogoImageUrl ?: return



        //1. get firebase reference
       // val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").push() //to push will generate automatic node for us in rtd

        val userContract = UserContract(
            usersRef!!.key!!,
            userReturedContracts!!.clientName,
            userReturedContracts!!.clientAddress,
            userReturedContracts!!.clientDate,
            userReturedContracts!!.clientDesc,
            currentUserId,
            profileLogoUri,
            clientSignImageUri,
            contractorSignImageUri,
            userReturedContracts!!.clientPrice,
            userReturedContracts!!.companyName,
            userReturedContracts!!.companyAddress,
            userReturedContracts!!.companyEmail

        )

        usersRef!!.setValue(userContract)
                .addOnSuccessListener {

                    Log.d("CaptureActivity", "User contracts ${userContract.clientSignUri}")

                    //=====hide progress bar===
                    mProgressBar!!.hide()

                    Log.d("CaptureActivity", "Contract Saved Successfully: ${usersRef!!.key}")

                    navigateToExistingContract()




                }



    }



    private fun navigateToExistingContract() {
        val intent = Intent(this, ExistingContractsActivity::class.java)

        //clear all activities on the stack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


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

            contractorSignUri = getImageUriFromBitmap(this, bitmap)
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