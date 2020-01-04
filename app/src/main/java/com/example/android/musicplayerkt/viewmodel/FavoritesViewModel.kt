package com.example.android.musicplayerkt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.musicplayerkt.database.LocalSong

class FavoritesViewModel : ViewModel() {
    private val _favoritesPlaylist = MutableLiveData<MutableList<LocalSong>>()
    val favoritesPlaylist : LiveData<MutableList<LocalSong>>
        get() = _favoritesPlaylist

    val updateFavorite : (LocalSong) -> Boolean =  {
        _favoritesPlaylist.value?.remove(it) ?: false
    }

    init {
        _favoritesPlaylist.value = mutableListOf()
    }

    fun refreshList(list : List<LocalSong>) {
        _favoritesPlaylist.value = list.toMutableList()
    }
}