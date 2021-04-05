package com.example.commanderscontracts.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class UserContract(var id: String, var clientName: String, var clientAddress: String, var clientDate: String, var clientDesc: String, var clientID: String, var clientProfileLogoUri: String, var clientSignUri: String,var contractorSignUri: String, var clientPrice: String, var companyName:String, var companyAddress:String, var companyEmail:String): Parcelable {


    constructor():this("","","","","","","","","","", "","","")

}
