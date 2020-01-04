package com.example.android.musicplayerkt.repository

import com.example.android.musicplayerkt.database.LocalDatabase
import com.example.android.musicplayerkt.database.LocalPlaylist
import com.example.android.musicplayerkt.database.LocalPlaylistSong
import com.example.android.musicplayerkt.database.LocalSong

class LocalRepository(private val database : LocalDatabase) {

    val localSongs = database.localSongDao.getAllSong()

    val localPlaylists = database.localPlaylistDao.getAllPlaylist()

    // Read every song from a playlist
    suspend fun getPlaylistSong(playlistId : Long) : Array<LocalSong> =
        database.localPlaylistSongDao.getSongsForPlaylist(playlistId)

    // Delete a song from a playlist
    suspend fun removePlaylistSong(playlistSong: LocalPlaylistSong) {
        database.localPlaylistSongDao.remove(playlistSong)
    }

    suspend fun insertPlaylistSong(playlistId: Long, songId : Long) {
        val playlistSong = LocalPlaylistSong(playlistId, songId)
        database.localPlaylistSongDao.insert(playlistSong)
    }

    suspend fun getSongNotInPlaylist(playlistId: Long) : Array<LocalSong> {
        return database.localPlaylistSongDao.getSongsNotInPlaylist(playlistId)
    }

    suspend fun insertPlaylist(localPlaylist: LocalPlaylist) {
        database.localPlaylistDao.insertPlaylist(localPlaylist)
    }

    suspend fun deletePlaylist(localPlaylist: LocalPlaylist) {
        database.localPlaylistDao.deletePlaylist(localPlaylist)
    }

    suspend fun updatePlaylist(localPlaylist: LocalPlaylist) {
        database.localPlaylistDao.updatePlaylist(localPlaylist)
    }

    suspend fun getPlaylist(playlistId: Long) : LocalPlaylist {
        return database.localPlaylistDao.getPlaylist(playlistId)
    }

    suspend fun insertSong(localSong: LocalSong) {
        database.localSongDao.insertSong(localSong)
    }

    fun deleteSong(localSong: LocalSong) {
        database.localSongDao.deleteSong(localSong)
    }

    fun updateSong(localSong : LocalSong) {
        database.localSongDao.updateSong(localSong)
    }

}