package com.example.music_player_app.domain.usecase

import com.example.music_player_app.domain.repository.RemoteTrackRepository


class DownloadAudioUseCase(private val repository: RemoteTrackRepository) {
    suspend operator fun invoke(trackId: Int): ByteArray? = repository.downloadAudio(trackId)
}