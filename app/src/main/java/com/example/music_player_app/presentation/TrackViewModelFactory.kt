package com.example.music_player_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.music_player_app.domain.repository.LocalTrackRepository
import com.example.music_player_app.domain.repository.TrackSyncRepository

class TrackViewModelFactory(
    private val localRepo: LocalTrackRepository,
    private val syncRepo: TrackSyncRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackViewModel(localRepo, syncRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}