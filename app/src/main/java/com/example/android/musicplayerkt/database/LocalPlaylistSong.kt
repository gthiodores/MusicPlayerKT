package com.example.android.musicplayerkt.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "playlist_song_join",
    primaryKeys = ["playlistId", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalPlaylist::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("playlistId"),
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = LocalSong::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("songId"),
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)

data class LocalPlaylistSong(
    var playlistId: Long,
    var songId: Long
)