package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import java.io.File

class AddTrackUseCase(private val repository: RemoteTrackRepository) {
    suspend operator fun invoke(track: Track, audioFile: File) = repository.addTrack(track, audioFile)
}