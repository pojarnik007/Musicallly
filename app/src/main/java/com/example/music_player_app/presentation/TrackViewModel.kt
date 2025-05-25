package com.example.music_player_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.usecase.AddTrackUseCase
import com.example.music_player_app.domain.usecase.DeleteTrackUseCase
import com.example.music_player_app.domain.usecase.GetTracksUseCase
import com.example.music_player_app.domain.usecase.DownloadAudioUseCase
import kotlinx.coroutines.launch

class TrackViewModel(
    private val getTracksUseCase: GetTracksUseCase,
    private val addTrackUseCase: AddTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> = _selectedTrack

    private val _audioData = MutableLiveData<ByteArray?>()
    val audioData: LiveData<ByteArray?> = _audioData

    fun selectTrack(track: Track) {
        _selectedTrack.value = track
    }

    fun loadTracks() {
        viewModelScope.launch { _tracks.value = getTracksUseCase() }
    }

    fun addTrack(track: Track, audioFile: java.io.File) {
        viewModelScope.launch { addTrackUseCase(track, audioFile); loadTracks() }
    }

    fun deleteTrack(trackId: String) {
        viewModelScope.launch { deleteTrackUseCase(trackId); loadTracks() }
    }

    fun downloadAudio(trackId: Int) {
        viewModelScope.launch {
            _audioData.value = downloadAudioUseCase(trackId)
        }
    }
}