package com.example.soundwave.data.local

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query


@Dao
interface LikedTrackDao {
    @Query("SELECT * FROM liked_tracks WHERE user_id = :userId ORDER BY createdAt DESC")
    suspend fun getForUser(userId: String): List<LikedTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LikedTrackEntity)

    @Query("DELETE FROM liked_tracks WHERE id = :id AND user_id = :userId")
    suspend fun deleteByIdForUser(id: String, userId: String)
}
