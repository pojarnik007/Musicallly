package com.example.music_player_app.presentation.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.music_player_app.R
import com.example.music_player_app.databinding.FragmentMiniPlayerBinding

class MiniPlayerFragment : Fragment() {
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.miniplayerContent.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlayerFragment())
                .addToBackStack("PlayerFragment")
                .commit()
        }

        playerViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            binding.root.isVisible = (track != null)
            if (track != null) {
                binding.textTitle.text = track.name
            }
        }

        playerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.buttonPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        binding.buttonPlayPause.setOnClickListener {
            playerViewModel.togglePlayPause()
        }
        binding.buttonPrev.setOnClickListener { playerViewModel.prev() }
        binding.buttonNext.setOnClickListener { playerViewModel.next() }
        binding.root.setOnClickListener {
            // Навигация на PlayerFragment (реализуйте если нужно)
        }

}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}