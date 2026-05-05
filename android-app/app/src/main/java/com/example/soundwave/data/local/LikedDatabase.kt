package com.example.soundwave.data.local

import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [LikedTrackEntity::class], version = 1)
abstract class LikedDatabase : RoomDatabase() {
    abstract fun likedTrackDao(): LikedTrackDao

    companion object {
        @Volatile
        private var INSTANCE: LikedDatabase? = null

        fun getInstance(context: Context): LikedDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    LikedDatabase::class.java,
                    "liked_tracks_db"
                ).build()
                INSTANCE = inst
                inst
            }
        }
    }
}
