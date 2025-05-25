package com.example.music_player_app.domain.repository

import com.example.music_player_app.domain.model.Track
import java.io.File

interface RemoteTrackRepository {
    suspend fun getTracks(): List<Track>
    suspend fun addTrack(track: Track, audioFile: File)
    suspend fun deleteTrack(trackId: String)
    suspend fun downloadAudio(trackId: Int): ByteArray?
}