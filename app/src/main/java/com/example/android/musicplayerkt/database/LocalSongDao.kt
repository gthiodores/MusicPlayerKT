package com.example.android.musicplayerkt.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocalSongDao {

    @Insert
    fun insertSong(localSong: LocalSong)

    @Update
    fun updateSong(localSong: LocalSong)

    @Delete
    fun deleteSong(localSong: LocalSong)

    @Query("SELECT * FROM local_song_table ORDER BY title ASC")
    fun getAllSong() : LiveData<List<LocalSong>>

}