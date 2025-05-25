package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.repository.RemoteTrackRepository

class GetTracksUseCase(private val repository: RemoteTrackRepository) {
    suspend operator fun invoke() = repository.getTracks()
}