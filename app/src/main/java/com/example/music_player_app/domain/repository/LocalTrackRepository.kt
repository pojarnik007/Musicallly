package com.example.music_player_app.domain.repository
import com.example.music_player_app.domain.model.TrackEntity

interface LocalTrackRepository {
    suspend fun getAllTracks(): List<TrackEntity>
    suspend fun insertTrack(track: TrackEntity)
    suspend fun insertTracks(tracks: List<TrackEntity>)
    suspend fun deleteTrack(trackId: Int)
}