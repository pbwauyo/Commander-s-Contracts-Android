package com.example.commanderscontracts

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import com.example.commanderscontracts.contracts.NewContractActivity
import com.example.commanderscontracts.models.UserContract
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_capture_client_signature.*
import kotlinx.android.synthetic.main.activity_capture_client_signature.client_signature_button
import java.io.ByteArrayOutputStream


class CaptureClientSignature : AppCompatActivity(),OnSignedCaptureListener {

    private var clientSignUri: Uri? = null

    private var pushKey: String? = null

    private var mProgressBar: ProgressDialog? = null

    var userReturedContracts: UserContract? = null

    var usersRef: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""

    private var clientSignUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_client_signature)

        //====Set title
        supportActionBar?.title = "CLIENT SIGNATURE"


        userReturedContracts = intent.getParcelableExtra<UserContract>(NewContractActivity.USER_KEY)


        val uid = FirebaseAuth.getInstance().uid?:""


        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersRef = FirebaseDatabase.getInstance().getReference("/user-contracts/$uid").push()

        //val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").push()

        storageRef = FirebaseStorage.getInstance().reference.child("signatures")

//        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
//
//        }



        mProgressBar = ProgressDialog(this)

        buttonShowClientDialog.setOnClickListener {
            showDialog()
        }


        client_signature_button.setOnClickListener {
            uploadUserImages()
        }

    }

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        Log.d("CaptureSignature", "CLIENT BITMAP: ${bitmap} ")

        // clientSignUri = getImageUriFromBitmap(this,bitmap)

        clientSignUri = getImageUriFromBitmap(this, bitmap)

        Log.d("ClientCaptureSignature", " Client BitmapUri ${clientSignUri}")

        client_signature_image_view.setImageBitmap(bitmap)

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



    //======UPLOAD IMAGE====
    private fun uploadUserImages() {
        val progressBar = ProgressDialog(this)

        progressBar.setMessage("Uploading image, Please wait...")
        progressBar.setTitle("Image Upload")
        progressBar.show()

        if(clientSignUri != null) {

            //https://stackoverflow.com/questions/61050721/download-url-is-getting-as-com-google-android-gms-tasks-zzu441942b-firebase-s/61061743#61061743

            //unique Id, so that image is not replaced for multiple upload
            //storing image by this name

            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>

            uploadTask = fileRef.putFile(clientSignUri!!)

            uploadTask.continueWith(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it

                    }


                }

                //return the download url
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{



                if(it.isSuccessful) {


                    it.result!!.addOnSuccessListener{ task ->

                        var downloadUrl = task.toString()
                        Log.d("CaptureSignature", "Download Url: ${downloadUrl}")


//                        //==========client signature
//                        if(isUserOrContractor ==  CaptureSignaturesActivity.WhichButton.CLIENT_SIGN.ordinal ) {
//                            val mapClientSignature = HashMap<String, Any>()
//                            mapClientSignature["clientSignUri"] = downloadUrl
//                            usersRef!!.updateChildren(mapClientSignature)
//
//
//                            //isUserOrContractor = WhichButton.DEFAULT.ordinal
//
//                        }



//                        } else {
//                            //====Contractor Signature====
//                            val mapContractorSignature = HashMap<String, Any>()
//                            mapContractorSignature["contractorSignUri"] = downloadUrl
//
//                            usersRef!!.updateChildren(mapContractorSignature)
//
//                            //change cover to default
//
//                            //isUserOrContractor = WhichButton.DEFAULT.ordinal
//
//
//
//                        }

                        //saveContractsToDB(downloadUrl, "")

                        saveContractsToDB(downloadUrl )



                    }




                    //=======dismiss progress bar========
                    progressBar.dismiss()

                }
            }

        }






    }




    private fun saveContractsToDB(clientSignImageUri: String) {

//        mProgressBar!!.setMessage("Saving Contract...")
//        mProgressBar!!.show()


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
            "",
            userReturedContracts!!.clientPrice,
            userReturedContracts!!.companyName,
            userReturedContracts!!.companyAddress,
            userReturedContracts!!.companyEmail

        )

        pushKey = usersRef!!.push().key

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
        val intent = Intent(this, CaptureContractorSignature::class.java)

        //clear all activities on the stack
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    //submit_signature_btn



}