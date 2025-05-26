package com.example.music_player_app.domain.repository

import com.example.music_player_app.domain.model.TrackEntity
import java.io.File

interface RemoteTrackRepository {
    suspend fun getTracks(): List<TrackEntity>
    suspend fun addTrack(track: TrackEntity, audioFile: File?)
    suspend fun deleteTrack(trackId: Int)
    suspend fun downloadAudio(trackId: Int): ByteArray?
}