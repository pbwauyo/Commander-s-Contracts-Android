package com.example.commanderscontracts.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.commanderscontracts.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application){

    private val repository = LocalStorage(application)

    val readFromDataStore = repository.readFromDataStore.asLiveData()

    fun saveToDataStore(isLoggedIn: Boolean) = viewModelScope.launch(Dispatchers.IO){

        repository.saveToDataStore(isLoggedIn)


    }



    //https://youtu.be/hEHVn9ATVjY?t=404
}