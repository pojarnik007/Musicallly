package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.repository.RemoteTrackRepository
import com.example.music_player_app.domain.model.TrackEntity
import java.io.File

class AddTrackUseCase(private val repository: RemoteTrackRepository) {
    suspend operator fun invoke(track: TrackEntity, audioFile: File? = null) =
        repository.addTrack(track, audioFile)
}