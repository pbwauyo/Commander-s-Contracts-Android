package com.example.commanderscontracts.models

class UserContract(val id: String, val clientName: String, val clientAddress: String, val clientDate: String, val clientDesc: String, val clientID: String, val clientProfileLogoUri: String, val clientPrice: String, clientSignUri:String) {
    constructor():this("","","", "","", "","", "", "")

}
