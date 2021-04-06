package com.example.commanderscontracts.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize


@Parcelize
class UserContract(
    var id: String,
    var clientName: String,
    var clientAddress: String,
    var clientDate: String,
    var clientDesc: String,
    var clientID: String,
    var clientProfileLogoUri: String,
    var clientSignUri: String,
    var contractorSignUri: String,
    var clientPrice: String,
    var companyName:String,
    var companyAddress:String,
    var companyEmail:String
    ): Parcelable {


    constructor():this("","","","","","","","","","", "","","")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "clientName" to clientName,
            "clientAddress" to clientAddress,
            "clientDate" to clientDate,
            "clientDesc" to clientDesc,
            "clientID" to clientID,
            "clientProfileLogoUri" to clientProfileLogoUri,
            "clientSignUri" to clientSignUri,
            "contractorSignUri" to contractorSignUri,
            "clientPrice" to clientPrice,
            "companyName" to companyName,
            "companyAddress" to companyAddress,
            "companyEmail" to companyEmail

        )
    }




}


//fun toMap(): Map<String, Any?> {
//    return mapOf(
//        "id" to id,
//        "clientName" to clientName,
//        "isBlocked" to isBlocked,
//    )
//}


