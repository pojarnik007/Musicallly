package com.example.music_player_app.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.music_player_app.R
import com.example.music_player_app.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.GONE
        playerViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            binding.textTitle.text = track?.name ?: ""
            binding.textArtist.text = track?.artist ?: ""
        }
        playerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.buttonPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        playerViewModel.position.observe(viewLifecycleOwner) { pos ->
            binding.seekBar.progress = pos
            binding.textCurrentTime.text = formatMillis(pos)
        }
        playerViewModel.duration.observe(viewLifecycleOwner) { dur ->
            binding.seekBar.max = dur
            binding.textTotalTime.text = formatMillis(dur)
        }

        binding.buttonCollapse.setOnClickListener {
            parentFragmentManager.popBackStack() // просто свернуть обратно
        }

        binding.buttonPlayPause.setOnClickListener { playerViewModel.togglePlayPause() }
        binding.buttonPrev.setOnClickListener { playerViewModel.prev() }
        binding.buttonNext.setOnClickListener { playerViewModel.next() }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) playerViewModel.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun formatMillis(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE
    }
}