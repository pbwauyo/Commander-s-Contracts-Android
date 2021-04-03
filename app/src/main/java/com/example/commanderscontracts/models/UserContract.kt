package com.example.commanderscontracts.models

class UserContract(var id: String, var clientName: String, var clientAddress: String, var clientDate: String, var clientDesc: String, var clientID: String, var clientProfileLogoUri: String, var clientSignUri: String,var contractorSignUri: String, var clientPrice: String) {


    constructor():this("","","","","","","","","","")

}
