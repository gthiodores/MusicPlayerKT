package com.example.android.musicplayerkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_playlist_table")
data class LocalPlaylist(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    var name : String = ""
)