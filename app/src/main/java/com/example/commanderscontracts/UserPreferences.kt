package com.example.commanderscontracts

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {
    private  val applicationContext = context.applicationContext

    private val dataStore: DataStore<Preferences>

    init {
        dataStore = applicationContext.createDataStore(
            name = "my_data_store"
        )

    }


    //===Get back the key ===
    val isLoggedIn: Flow<Boolean?>

        get() = dataStore.data.map {
                preferences ->

            preferences[KEY_LOGGED_IN]

        }


    suspend fun saveIsLoggedIn(isLoggedIn: Boolean){
        dataStore.edit {
                preferences ->

            preferences[KEY_LOGGED_IN] = isLoggedIn

        }
    }

    companion object{
        private val KEY_LOGGED_IN = preferencesKey<Boolean>("key_is_logged_in")
    }
}