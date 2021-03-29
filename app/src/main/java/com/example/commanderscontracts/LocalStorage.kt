package com.example.commanderscontracts

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "my_preference"

class LocalStorage(appContext: Context)  {

    private  val dataStore: DataStore<Preferences> = appContext.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend  fun saveToDataStore(isLoggedIn: Boolean) {
        dataStore.edit {
                preference ->

            preference[DataStorePrefKeys.USER_LOGGED_IN] = isLoggedIn

        }
    }

    //reading

    val readFromDataStore: Flow<Boolean> = dataStore.data
        .catch {
                exception ->
            if (exception is IOException){
                Log.d("Datastore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map {
                preference ->

            val isUserLoggedIn = preference[DataStorePrefKeys.USER_LOGGED_IN] ?: false

            isUserLoggedIn


        }




}