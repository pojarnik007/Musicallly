package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.repository.TrackRepository

class GetTracksUseCase(private val repository: TrackRepository) {
    operator fun invoke(): List<Track> = repository.getTracks()
}