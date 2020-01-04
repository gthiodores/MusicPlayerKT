package com.example.android.musicplayerkt.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AddSongViewModelFactory (
    private val application : Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSongViewModel::class.java)) {
            return AddSongViewModel(application) as T
        }
        throw IllegalArgumentException("Unkown View Model Class")
    }

}