package com.example.music_player_app.domain.repository

import com.example.music_player_app.domain.model.Track

interface TrackRepository {
    fun getTracks(): List<Track>
    fun addTrack(track: Track)
    fun deleteTrack(trackId: String)
}