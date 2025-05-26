package com.example.music_player_app.di

import android.content.Context
import com.example.music_player_app.data.repository.RemoteTrackRepositoryImpl
import com.example.music_player_app.domain.local.AppDatabase
import com.example.music_player_app.data.repository.LocalTrackRepositoryImpl
import com.example.music_player_app.data.repository.TrackSyncRepositoryImpl
import com.example.music_player_app.presentation.TrackViewModelFactory

object ServiceLocator {
    fun provideTrackViewModelFactory(context: Context): TrackViewModelFactory {
        val localRepo = LocalTrackRepositoryImpl(context)
        val remoteRepo = RemoteTrackRepositoryImpl()
        val syncRepo = TrackSyncRepositoryImpl(context, remoteRepo, localRepo)
        return TrackViewModelFactory(localRepo, syncRepo)
    }
}