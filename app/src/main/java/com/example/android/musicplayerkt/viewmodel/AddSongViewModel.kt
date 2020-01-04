package com.example.android.musicplayerkt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.musicplayerkt.database.LocalDatabase
import com.example.android.musicplayerkt.database.LocalSong
import com.example.android.musicplayerkt.repository.LocalRepository
import kotlinx.coroutines.*

class AddSongViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocalRepository =
        LocalRepository(LocalDatabase.getInstance(application))

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _songsToAdd = MutableLiveData<Array<LocalSong>>()
    val songsToAdd : LiveData<Array<LocalSong>>
        get() = _songsToAdd

    private val _addSongsCompleted = MutableLiveData<Boolean>()
    val addSongsCompleted : LiveData<Boolean>
        get() = _addSongsCompleted

    init {
        _addSongsCompleted.value = false
    }

    fun onConfirmButtonClick(playlistId: Long, items : List<LocalSong>) {
        uiScope.launch {
            _addSongsCompleted.value = insertPlaylistSong(playlistId = playlistId, items = items)
        }
    }

    private suspend fun insertPlaylistSong(playlistId: Long, items: List<LocalSong>) : Boolean {
        return withContext(Dispatchers.IO) {
            items.forEach {item ->
                repository.insertPlaylistSong(playlistId, item.id)
            }
            true
        }
    }

    fun getSongsToAdd(playlistId: Long) {
        uiScope.launch {
            _songsToAdd.value = getSongsNotInPlaylist(playlistId)
        }
    }

    private suspend fun getSongsNotInPlaylist(playlistId: Long) : Array<LocalSong> {
        return withContext(Dispatchers.IO) {
            repository.getSongNotInPlaylist(playlistId)
        }
    }

    fun onDoneNavigating() {
        _addSongsCompleted.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}