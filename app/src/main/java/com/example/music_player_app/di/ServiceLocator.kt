package com.example.music_player_app.di

import TrackViewModelFactory
import com.example.music_player_app.data.repository.InMemoryTrackRepository
import com.example.music_player_app.domain.usecase.AddTrackUseCase
import com.example.music_player_app.domain.usecase.DeleteTrackUseCase
import com.example.music_player_app.domain.usecase.GetTracksUseCase

object ServiceLocator {
    fun provideTrackViewModelFactory(): TrackViewModelFactory {
        val repo = InMemoryTrackRepository
        return TrackViewModelFactory(
            GetTracksUseCase(repo),
            AddTrackUseCase(repo),
            DeleteTrackUseCase(repo)
        )
    }
}