package com.example.android.musicplayerkt.database

import androidx.room.*

@Dao
interface LocalPlaylistSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlistSong: LocalPlaylistSong)

    @Delete
    fun remove(playlistSong: LocalPlaylistSong)

    @Query("""
        SELECT * FROM local_song_table
        INNER JOIN playlist_song_join
        ON local_song_table.id = playlist_song_join.songId
        WHERE playlist_song_join.playlistId = :playlistId
    """)
    fun getSongsForPlaylist(playlistId : Long) : Array<LocalSong>

    @Query("""
        SELECT * FROM local_song_table
        WHERE local_song_table.id
        NOT IN(
            SELECT local_song_table.id FROM local_song_table
            INNER JOIN playlist_song_join
            ON local_song_table.id = playlist_song_join.songId
            WHERE playlist_song_join.playlistId = :playlistId
        )
    """)
    fun getSongsNotInPlaylist(playlistId : Long) : Array<LocalSong>

}