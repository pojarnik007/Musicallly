package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.repository.TrackRepository

class DeleteTrackUseCase(private val repository: TrackRepository) {
    operator fun invoke(trackId: String) = repository.deleteTrack(trackId)
}