package com.example.music_player_app.data.repository

import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.repository.TrackRepository

object InMemoryTrackRepository : TrackRepository {
    private val tracks = mutableListOf<Track>()
    override fun getTracks(): List<Track> = tracks.toList()
    override fun addTrack(track: Track) { tracks.add(track) }
    override fun deleteTrack(trackId: String) { tracks.removeAll { it.id == trackId } }
}