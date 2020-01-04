package com.example.android.musicplayerkt.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocalPlaylistDao {

    @Insert
    fun insertPlaylist(localPlaylist: LocalPlaylist)

    @Update
    fun updatePlaylist(localPlaylist: LocalPlaylist)

    @Delete
    fun deletePlaylist(localPlaylist: LocalPlaylist)

    @Query("SELECT * FROM local_playlist_table ORDER BY name ASC")
    fun getAllPlaylist() : LiveData<List<LocalPlaylist>>

    @Query("SELECT * FROM local_playlist_table WHERE id = :playlistId")
    fun getPlaylist(playlistId : Long) : LocalPlaylist

}