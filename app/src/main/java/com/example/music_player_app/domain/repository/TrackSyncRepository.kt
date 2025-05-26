package com.example.music_player_app.domain.repository

import com.example.music_player_app.domain.model.TrackEntity

interface TrackSyncRepository {
    suspend fun syncTracks()
    suspend fun addTrackRemote(track: TrackEntity)
    suspend fun deleteTrackRemote(trackId: Int)
}