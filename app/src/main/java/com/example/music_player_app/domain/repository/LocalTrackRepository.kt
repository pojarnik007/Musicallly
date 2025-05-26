package com.example.music_player_app.domain.repository

import com.example.music_player_app.domain.model.TrackEntity

interface LocalTrackRepository {
    suspend fun getAllTracks(): List<TrackEntity>
    suspend fun insertTrack(track: TrackEntity)
    suspend fun deleteTrack(trackId: Int)
    suspend fun insertAll(tracks: List<TrackEntity>)
    suspend fun deleteNotIn(ids: List<Int>)
}