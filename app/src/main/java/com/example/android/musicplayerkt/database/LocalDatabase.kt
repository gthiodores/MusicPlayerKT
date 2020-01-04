package com.example.android.musicplayerkt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LocalSong::class, LocalPlaylist::class, LocalPlaylistSong::class],
    version = 11,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract val localSongDao : LocalSongDao

    abstract val localPlaylistDao : LocalPlaylistDao

    abstract val localPlaylistSongDao : LocalPlaylistSongDao

    companion object {
        @Volatile
        private var INSTANCE : LocalDatabase? = null

        fun getInstance(context: Context) : LocalDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "database_local"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}