package com.example.commanderscontracts

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.commanderscontracts.contracts.NewContractActivity
import com.example.commanderscontracts.models.User
import com.example.commanderscontracts.models.UserContract
import com.example.commanderscontracts.views.ContractListRow.Companion.USER_CONTRACT_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfFormXObject
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_p_d_f.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class CreatePDFActivity : AppCompatActivity() {
    
    var userContracts: UserContract? = null
    var currentUser: User? = null



    //private val FILE_PRINT:String = "PDFPrint.pdf"
    companion object{
        private val FILE_PRINT:String = "PDFPrint.pdf"

        fun getBitMapFromUri(context: Context, model: UserContract, document: Document):Observable<UserContract>{

            return Observable.fromCallable{
                val bitMap = Glide.with(context)
                    .asBitmap()
                    .load(model.clientProfileLogoUri)
                    .submit()
                    .get()

                val  image = Image.getInstance(bitMapToByteArray(bitMap))


//                val xObject = PdfFormXObject(Rectangle(850, 600))
//                val xObjectCanvas = PdfCanvas(xObject, pdfDoc)
//                xObjectCanvas.ellipse(0, 0, 850, 600)
//                xObjectCanvas.clip()
//                xObjectCanvas.newPath()
//                xObjectCanvas.addImage(image, 100, 0, 0, 100, 0, -600)
//                val clipped = Image(xObject)
//                clipped.scale(0.5f, 0.5f)
//                document.add(clipped)

                image.scaleToFit(80f, 80f)
                document.add(image)
                model
            }


        }



        fun getClientSignBitMapFromUri(context: Context, model: UserContract, document: Document):Observable<UserContract>{

            return Observable.fromCallable{
                val bitMap = Glide.with(context)
                        .asBitmap()
                        .load(model.clientSignUri)
                        .submit()
                        .get()

                val  clientImage = Image.getInstance(bitMapToByteArray(bitMap))



                clientImage.scaleAbsolute(100f, 100f)
                document.add(clientImage)
                model
            }
        }



        fun getContractorSignBitMapFromUri(
            context: Context,
            model: UserContract,
            document: Document
        ):Observable<UserContract>{

            return Observable.fromCallable{
                val bitMap = Glide.with(context)
                        .asBitmap()
                        .load(model.contractorSignUri)
                        .submit()
                        .get()

                val  contractorImage = Image.getInstance(bitMapToByteArray(bitMap))

                contractorImage.scaleAbsolute(100f, 100f)
                document.add(contractorImage)
                model
            }
        }




        private fun bitMapToByteArray(bitMap: Bitmap?):ByteArray{
            val stream = ByteArrayOutputStream()
            bitMap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()

        }
    }


    private val appPath:String

        get(){
            val dir = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator
                        + resources.getString(R.string.app_name)
                        + File.separator
            )

            if(!dir.exists()) dir.mkdir()

            return dir.path + File.separator


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_p_d_f)

        fetchCurrentUser()

        userContracts = intent.getParcelableExtra<UserContract>(USER_CONTRACT_KEY)

        supportActionBar?.title = userContracts?.clientName



        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    print_btn.setOnClickListener {
                        createPDFFile(StringBuilder(appPath).append(FILE_PRINT).toString())
                    }

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@CreatePDFActivity,
                        "Please enable storage",
                        Toast.LENGTH_LONG
                    ).show()

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            })
            .check()


    }





    private fun createPDFFile(path: String) {
        if(File(path).exists())File(path).delete()

        try{
            val document = Document()
            //save pdf

            PdfWriter.getInstance(document, FileOutputStream(path))

            //open
            document.open()

            //setting
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("Commander")
            document.addCreator("Tech")

            //font setting
            val colorAccent = BaseColor(0, 153, 204, 255)
            val fontSize = 20.0f


            //custom font
            val fontName = BaseFont.createFont(
                "assets/fonts/brandon_medium.otf",
                "UTF-8",
                BaseFont.EMBEDDED
            )

            //create Title of Document
            val titleFont = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)

            val bigTitleFont = Font(fontName, 36.0f, Font.NORMAL, colorAccent)

            PDFUtils.addNewItem(document, "CONTRACT", Element.ALIGN_CENTER, bigTitleFont)


            //Add more
            PDFUtils.addLineSpace(document)
            PDFUtils.addNewItem(document, "Details", Element.ALIGN_CENTER, titleFont)
            PDFUtils.addLineSeparator(document)




//https://www.raywenderlich.com/books/reactive-programming-with-kotlin/v2.0/chapters/2-observables
            //use RxJava to fetch image and add to PDF
            Observable.just(userContracts!!).
            flatMap { model: UserContract ->
                getBitMapFromUri(this, model, document) }

//            flatMap { model:UserContract ->
//                getClientSignBitMapFromUri(this, model,document) }
//
//            flatMap { model:UserContract ->
//                getContractorSignBitMapFromUri(this, model,document) }

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ model: UserContract ->
                    //on next

                    PDFUtils.addNewItem(
                        document,
                        "CONTRACTOR DETAILS",
                        Element.ALIGN_RIGHT,
                        bigTitleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        "",
                        model.companyName!!,
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        "",
                        model.companyAddress!!,
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        "",
                        model.companyEmail!!,
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addLineSeparator(document)

                    PDFUtils.addNewItem(
                        document,
                        "CLIENT DETAILS",
                        Element.ALIGN_LEFT,
                        bigTitleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        model.clientName!!,
                        "",
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        model.clientAddress!!,
                        "",
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addNewItemWithLeftAndRight(
                        document,
                        model.clientDate!!,
                        "",
                        titleFont,
                        titleFont
                    )
                    PDFUtils.addLineSeparator(document)


                    PDFUtils.addNewItem(
                        document,
                        "CONTRACT DESCRIPTION",
                        Element.ALIGN_RIGHT,
                        bigTitleFont
                    )
                    PDFUtils.addNewItem(document, model.clientDesc!!, Element.ALIGN_LEFT, titleFont)
                    PDFUtils.addLineSeparator(document)
                    PDFUtils.addNewItem(
                        document,
                        "PRICE : $${model.clientPrice!!}",
                        Element.ALIGN_LEFT,
                        titleFont
                    )
                    PDFUtils.addLineSeparator(document)


                    PDFUtils.addNewItem(
                        document,
                        "FROM : ${model.clientName!!}",
                        Element.ALIGN_RIGHT,
                        titleFont
                    )
                    PDFUtils.addLineSeparator(document)


                    PDFUtils.addNewItem(document, "SIGNATURES", Element.ALIGN_CENTER, bigTitleFont)

                    Observable.just(userContracts!!).
                    flatMap { model: UserContract ->
                        getClientSignBitMapFromUri(this, model, document) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())


                }, { t: Throwable? ->

                    //on error toast
                    Toast.makeText(this, t!!.message, Toast.LENGTH_LONG).show()

                }, {
                    //Oncomplete
                    PDFUtils.addLineSpace(document)
                    PDFUtils.addLineSpace(document)

                    //close
                    document.close()
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

                    printPDF()

                })



        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: DocumentException){
            e.printStackTrace()
        }finally {


        }

    }

    private fun printPDF() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try{
            val printDocumentAdapter = PDFDocumentAdapter(
                StringBuilder(appPath).append(FILE_PRINT).toString(), FILE_PRINT
            )
            printManager.print("Document", printDocumentAdapter, PrintAttributes.Builder().build())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }





    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d(
                    "NewContract",
                    "Current User: ${NewContractActivity.currentUser?.companyName}"
                )

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}