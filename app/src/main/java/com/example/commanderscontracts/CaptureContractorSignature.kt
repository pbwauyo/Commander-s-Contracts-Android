package com.example.commanderscontracts

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.commanderscontracts.contracts.ExistingContractsActivity
import com.example.commanderscontracts.contracts.NewContractActivity
import com.example.commanderscontracts.models.UserContract
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_capture_contractor_signature.*
import java.io.ByteArrayOutputStream

class CaptureContractorSignature : AppCompatActivity(),OnSignedCaptureListener {

    private var contractorSignUri: Uri? = null

   private var pushKey: String? = null

    private var mProgressBar: ProgressDialog? = null

    var userReturedContracts: UserContract? = null
    var currentUserContracts: UserContract? = null

    var usersRef: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private var storageRef: StorageReference? = null
    private var uid: String? = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_contractor_signature)

        supportActionBar?.title = "CONTRACTOR SIGNATURE"

        pushKey = intent.getStringExtra("PUSHKEY")


         uid = FirebaseAuth.getInstance().uid?:""


        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersRef = FirebaseDatabase.getInstance().getReference("/user-contracts/$uid").push()

        //val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").push()

        storageRef = FirebaseStorage.getInstance().reference.child("signatures")

        mProgressBar = ProgressDialog(this)

        fetchExistingContracts()

        buttonShowContractorDialog.setOnClickListener {
            showDialog()
        }




        contractor_signature_button.setOnClickListener {

            uploadContractorSignature()

        }
    }

    private fun uploadContractorSignature() {
        val progressBar = ProgressDialog(this)

        progressBar.setMessage("Uploading image, Please wait...")
        progressBar.setTitle("Image Upload")
        progressBar.show()

        if(contractorSignUri != null) {

            //https://stackoverflow.com/questions/61050721/download-url-is-getting-as-com-google-android-gms-tasks-zzu441942b-firebase-s/61061743#61061743

            //unique Id, so that image is not replaced for multiple upload
            //storing image by this name

            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>

            uploadTask = fileRef.putFile(contractorSignUri!!)

            uploadTask.continueWith(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
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

                        saveContractsToDB(downloadUrl)



                    }




                    //=======dismiss progress bar========
                    progressBar.dismiss()

                }
            }

        }



    }

    private fun saveContractsToDB(contractorSignImageUri: String) {

        val currentUserId  = FirebaseAuth.getInstance().uid ?: return

        val profileLogoUri = NewContractActivity.currentUser?.companyLogoImageUrl ?: return




        //val userUID =   FirebaseAuth.getInstance().uid?:""

        val ref = FirebaseDatabase.getInstance().getReference("/user-contracts")

        val database = FirebaseDatabase.getInstance().getReference("/user-contracts/$uid")

        val databaseKey = database.push().key


//        val pushnode = ref.child(key!!)
//
//            //.child(ref!!.key!!).push()
//
//        Log.d("CaptureActivity", "Ref Key: ${key}")
//
//        Log.d("CaptureActivity", "DB ref: ${ref}")


        val key: String? = ref.key

        //val keyPush = key.push()

        val dkey = database.push().key

        Log.d("TAG", "PUSH KEY CONTRACTOR ${pushKey}")





        if (pushKey == null) {
            Log.w("TAG", "Couldn't get push key for ${uid}")
            return
        }



        val userContract = UserContract(
            currentUserContracts!!.id,
            currentUserContracts!!.clientName,
            currentUserContracts!!.clientAddress,
            currentUserContracts!!.clientDate,
            currentUserContracts!!.clientDesc,
            currentUserId,
            profileLogoUri,
            currentUserContracts!!.clientSignUri,
            contractorSignImageUri,
            currentUserContracts!!.clientPrice,
            currentUserContracts!!.companyName,
            currentUserContracts!!.companyAddress,
            currentUserContracts!!.companyEmail

        )


        val postValues = userContract.toMap()


        val childUpdates = hashMapOf<String, Any>(
            "$dkey" to postValues,

        )


//        database.updateChildren(childUpdates).addOnSuccessListener {
//
//            //=====hide progress bar===
//            mProgressBar!!.hide()
//
//            Log.d("CaptureActivity", "Contract Saved Successfully: ${usersRef!!.key}")
//
//            navigateToExistingContract()
//
//        }

        //val post = Post(userId, username, title, body)





        database.child(currentUserContracts!!.companyEmail).child("contractorSignUri").setValue(contractorSignImageUri).addOnSuccessListener {
            Log.d("CaptureActivity", "User contracts ${userContract.contractorSignUri}")

            //=====hide progress bar===
            mProgressBar!!.hide()

            Log.d("TAG", "Contract Saved Successfully: ${currentUserContracts!!.id}")

            navigateToExistingContract()
        }



    }

    private fun navigateToExistingContract() {
        val intent = Intent(this, ExistingContractsActivity::class.java)

        //clear all activities on the stack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private fun fetchExistingContracts() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-contracts/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserContracts = snapshot.getValue(UserContract::class.java)
                // Log.d("LatestMessage","Current User: ${currentUserContracts?.}")

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {

        Log.d("CaptureSignature", "CLIENT BITMAP: ${bitmap} ")

        // clientSignUri = getImageUriFromBitmap(this,bitmap)

        contractorSignUri = getImageUriFromBitmap(this, bitmap)

        Log.d("ClientCaptureSignature", " Client BitmapUri ${contractorSignUri}")

        contractor_signature_image_view.setImageBitmap(bitmap)




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



}