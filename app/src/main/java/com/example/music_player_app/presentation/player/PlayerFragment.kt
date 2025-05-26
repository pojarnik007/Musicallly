package com.example.music_player_app.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.music_player_app.R
import com.example.music_player_app.databinding.FragmentPlayerBinding
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.presentation.TrackViewModel

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                currentTrackIndex = 0
                playTrack(tracks[currentTrackIndex])
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause() else it.start()
            }
        }
        binding.buttonPrev.setOnClickListener {
            val tracks = viewModel.tracks.value ?: return@setOnClickListener
            if (tracks.isNotEmpty()) {
                currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size
                playTrack(tracks[currentTrackIndex])
            }
        }
        binding.buttonNext.setOnClickListener {
            val tracks = viewModel.tracks.value ?: return@setOnClickListener
            if (tracks.isNotEmpty()) {
                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                playTrack(tracks[currentTrackIndex])
            }
        }
    }

    private fun playTrack(track: TrackEntity) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.localPath)
            setOnPreparedListener { start() }
            prepareAsync()
        }
        binding.textTitle.text = track.name
        binding.textArtist.text = track.artist
        binding.imageCover.setImageResource(R.drawable.music_note)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}