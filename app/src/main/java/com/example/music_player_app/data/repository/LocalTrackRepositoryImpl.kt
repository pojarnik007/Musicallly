package com.example.music_player_app.data.repository

import android.content.Context
import com.example.music_player_app.domain.local.AppDatabase
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository

class LocalTrackRepositoryImpl(context: Context) : LocalTrackRepository {
    private val dao = AppDatabase.getInstance(context).trackDao()

    override suspend fun getAllTracks(): List<TrackEntity> = dao.getAll()
    override suspend fun insertTrack(track: TrackEntity) = dao.insert(track)
    override suspend fun deleteTrack(trackId: Int) = dao.deleteById(trackId)
    override suspend fun insertAll(tracks: List<TrackEntity>) = dao.insertAll(tracks)
    override suspend fun deleteNotIn(ids: List<Int>) = dao.deleteNotIn(ids)
}