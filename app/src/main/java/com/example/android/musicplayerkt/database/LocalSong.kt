package com.example.android.musicplayerkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_song_table")
data class LocalSong (
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    var title : String = "",

    var artist : String = "",

    var duration : Long = 0L,

    var uri : String = "",

    var favourite : Boolean = false
)