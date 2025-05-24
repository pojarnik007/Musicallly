package com.example.music_player_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.usecase.AddTrackUseCase
import com.example.music_player_app.domain.usecase.DeleteTrackUseCase
import com.example.music_player_app.domain.usecase.GetTracksUseCase

class TrackViewModel(
    private val getTracksUseCase: GetTracksUseCase,
    private val addTrackUseCase: AddTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> = _selectedTrack

    fun selectTrack(track: Track) {
        _selectedTrack.value = track
    }

    fun loadTracks() { _tracks.value = getTracksUseCase() }
    fun addTrack(track: Track) { addTrackUseCase(track); loadTracks() }
    fun deleteTrack(trackId: String) { deleteTrackUseCase(trackId); loadTracks() }
}