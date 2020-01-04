package com.example.android.musicplayerkt.viewmodel

import android.app.Application
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.example.android.musicplayerkt.database.LocalDatabase
import com.example.android.musicplayerkt.database.LocalPlaylist
import com.example.android.musicplayerkt.database.LocalPlaylistSong
import com.example.android.musicplayerkt.database.LocalSong
import com.example.android.musicplayerkt.repository.LocalRepository
import kotlinx.coroutines.*

class LocalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocalRepository =
        LocalRepository(LocalDatabase.getInstance(application))

    // Kotlin COROUTINE
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // The list of user playlist
    val allPlaylist = repository.localPlaylists

    // The current selected playlist
    private val _currentPlaylist = MutableLiveData<LocalPlaylist?>()
    val currentPlaylist: LiveData<LocalPlaylist?>
        get() = _currentPlaylist

    // List of songs in the current playlist
    private val _playlistSong = MutableLiveData<List<LocalSong>?>()
    val playlistSong: LiveData<List<LocalSong>?>
        get() = _playlistSong

    // List of songs in the database
    val allSongs = repository.localSongs

    // Released is the release state of the media player, true if the if doesn't have a resource
    // or the resource has been released
    private val _released = MutableLiveData<Boolean>()
    val controlVisible = Transformations.map(_released) {
        it == false
    }

    private lateinit var mediaPlayer: MediaPlayer

    // Current selected song from the playlistSong
    private val _currentSong = MutableLiveData<LocalSong?>()
    val currentSong: LiveData<LocalSong?>
        get() = _currentSong

    // Media Player is playing status
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private val _playbackVolume = MutableLiveData<Int>()
    val playbackVolume : LiveData<Int>
        get() = _playbackVolume

    init {
        _currentSong.value = null
        _released.value = true
        _playbackVolume.value = 100
        _isPlaying.value = false
    }

    override fun onCleared() {
        super.onCleared()
        if (_released.value == false)
            mediaPlayer.release()
        viewModelJob.cancel()
    }

    private fun playSong() {
        // Release media player if it contains an audio file
        if (_released.value == false) {
            stopPlayback()
        }

        val uriPath = Uri.parse(_currentSong.value!!.uri)

        MediaPlayer.create(getApplication() as Application, uriPath).run {
            when (this) {
                null -> {
                    Toast.makeText(
                        getApplication() as Application,
                        "The song is missing from it's recorded directory",
                        Toast.LENGTH_SHORT
                    ).show()
                    _playlistSong.value?.let { list ->
                        val mutableList = list.toMutableList()
                        mutableList.remove(_currentSong.value!!)
                        _playlistSong.value = mutableList
                    }
                    _currentSong.value?.run {
                        removeSong(_playlistSong.value!!.indexOf(this))
                    }
                }
                else -> {
                    mediaPlayer = this

                    // Releases media player on track playback completion
                    mediaPlayer.setOnCompletionListener {
                        stopPlayback()
                        onNextButtonClicked()
                    }

                    // Start track playback
                    mediaPlayer.start()
                    mediaPlayer.setVolume(
                        _playbackVolume.value?.toFloat()?.div(100) ?: 1F,
                        _playbackVolume.value?.toFloat()?.div(100) ?: 1F
                    )
                    _released.value = false
                    _isPlaying.value = true
                }
            }
        }
    }

    fun onSongSelected(song: LocalSong) {
        if (_released.value == false)
            stopPlayback()
        _currentSong.value = song
        playSong()
    }

    fun stopPlayback() {
        mediaPlayer.stop()
        mediaPlayer.release()
        _released.value = true
        _isPlaying.value = false
    }

    // Button play and pause click event
    fun onPlayPauseButtonClicked() {
        when (mediaPlayer.isPlaying) {
            true -> {
                mediaPlayer.pause()
                _isPlaying.value = false
            }
            else -> {
                mediaPlayer.start()
                _isPlaying.value = true
            }
        }
    }

    // Button next click event
    fun onNextButtonClicked() {
        _playlistSong.value?.let { playlist ->
            when (playlist.isNullOrEmpty()) {
                true -> {
                    stopPlayback()
                    Toast.makeText(
                        getApplication() as Application,
                        "Playlist is empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val index = playlist.indexOf(_currentSong.value)
                    when (index < playlist.size.minus(1)) {
                        true -> _currentSong.value = playlist[index.plus(1)]
                        else -> _currentSong.value = playlist[0]
                    }
                    playSong()
                }
            }
        }
    }

    // Button previous click event
    fun onPreviousButtonClicked() {
        _playlistSong.value?.let { playlist ->
            when (playlist.isNullOrEmpty()) {
                true -> {
                    stopPlayback()
                    Toast.makeText(
                        getApplication() as Application,
                        "Playlist is empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val index = playlist.indexOf(_currentSong.value)
                    when (index > 0) {
                        true -> _currentSong.value = playlist[index.minus(1)]
                        else -> _currentSong.value = playlist[playlist.size.minus(1)]
                    }
                    playSong()
                }
            }
        }
    }

    fun onVolumeControlPositionChanged(position : Int) {
        _playbackVolume.value = position
        if(_isPlaying.value == true) {
            val mediaPlayerVolume = position.toFloat().div(100)
            mediaPlayer.setVolume(mediaPlayerVolume, mediaPlayerVolume)
        }
    }

    fun testPlaylistChanged(list: List<LocalSong>) {
        _playlistSong.value = list
    }

    fun onPlaylistSelected(playlistId: Long) {
        when (playlistId) {
            -1L -> _currentPlaylist.value = null
            else -> viewModelScope.launch {
                _currentPlaylist.value = getCurrentPlaylist(playlistId)
            }
        }
    }

    private suspend fun getCurrentPlaylist(playlistId: Long): LocalPlaylist {
        return withContext(Dispatchers.IO) {
            repository.getPlaylist(playlistId)
        }
    }

    fun onPlaylistChanged() {
        when (_currentPlaylist.value?.id ?: -1L) {
            -1L -> _playlistSong.value = allSongs.value
            else -> viewModelScope.launch {
                _playlistSong.value = getPlaylistSongs(_currentPlaylist.value!!.id).asList()
            }
        }
    }

    private suspend fun getPlaylistSongs(playlistId: Long): Array<LocalSong> {
        return withContext(Dispatchers.IO) {
            repository.getPlaylistSong(playlistId)
        }
    }

    fun insertSong(data: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(getApplication() as Application, data)

        val music = LocalSong(
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString(),
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString(),
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong(),
            uri = data.toString()
        )

        uiScope.launch(Dispatchers.IO) {
            repository.insertSong(music)
        }
    }

    fun removeSong(position: Int) {
        uiScope.launch {
            _playlistSong.value?.let { list ->
                deleteSong(list[position])
                _playlistSong.value = _playlistSong.value!!.filter {
                    it != list[position]
                }
            }
        }
    }

    private suspend fun deleteSong(localSong: LocalSong) {
        uiScope.launch(Dispatchers.IO) {
            if (_currentPlaylist.value == null)
                repository.deleteSong(localSong)
            else {
                val playlistSong = LocalPlaylistSong(
                    songId = localSong.id,
                    playlistId = _currentPlaylist.value!!.id
                )
                repository.removePlaylistSong(playlistSong)
            }
        }
    }

    fun onFavouriteClicked(localSong: LocalSong) {
        uiScope.launch {
            localSong.favourite = localSong.favourite.not()
            updateSong(localSong)
        }
    }

    private suspend fun updateSong(localSong: LocalSong) {
        return withContext(Dispatchers.IO) {
            repository.updateSong(localSong)
        }
    }

    fun insertPlaylist(playlist: LocalPlaylist) {
        uiScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlist)
        }
    }

    fun onPlaylistSwipe(position: Int) {
        uiScope.launch {
            allPlaylist.value?.let { playlists ->
                val playlist = playlists[position]
                if (playlist == _currentPlaylist.value ?: LocalPlaylist())
                    _currentPlaylist.value = null
                removePlaylist(playlist)
            }
        }
    }

    private suspend fun removePlaylist(playlist: LocalPlaylist) {
        uiScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(playlist)
        }
    }
}