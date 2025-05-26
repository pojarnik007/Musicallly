package com.example.music_player_app.domain.local

import androidx.room.*
import com.example.music_player_app.domain.model.TrackEntity

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks")
    suspend fun getAll(): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Int)

    @Query("DELETE FROM tracks WHERE id NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<Int>)
}