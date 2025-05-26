package com.example.music_player_app.di

import android.content.Context
import com.example.music_player_app.data.repository.RemoteTrackRepositoryImpl
import com.example.music_player_app.domain.local.AppDatabase
import com.example.music_player_app.data.repository.LocalTrackRepositoryImpl
import com.example.music_player_app.data.repository.TrackSyncRepositoryImpl
import com.example.music_player_app.presentation.TrackViewModelFactory

object ServiceLocator {
    fun provideTrackViewModelFactory(context: Context): TrackViewModelFactory {
        val db = AppDatabase.getInstance(context)
        val localRepo = LocalTrackRepositoryImpl(db.trackDao())
        val remoteRepo = RemoteTrackRepositoryImpl()
        val syncRepo = TrackSyncRepositoryImpl(remoteRepo, db.trackDao())
        return TrackViewModelFactory(localRepo, syncRepo)
    }
}