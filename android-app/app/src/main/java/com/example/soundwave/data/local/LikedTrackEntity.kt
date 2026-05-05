package com.example.soundwave.data.local

//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "liked_tracks")
data class LikedTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val styleName: String,
    val duration: Int,
    val createdAt: String,
    val audioUrl: String,
    val coverUrl: String,
    val lyrics: String?,
    val username: String?,
    @ColumnInfo(name = "user_id") val userId: String
)
