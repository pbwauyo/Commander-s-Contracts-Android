package com.example.commanderscontracts

import androidx.datastore.preferences.preferencesKey

object DataStorePrefKeys {

    val USER_LOGGED_IN = preferencesKey<Boolean>("user_logged_in")
}