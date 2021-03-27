package com.example.commanderscontracts.models

///===Class User ===
class User(val uid: String, val companyName:String, val companyAddress:String, val companyPhone:String,val companyEmail:String, val companyLogoImageUrl:String)

{
    constructor():this("","","", "", "", "") //solves no argument constructor error
}

