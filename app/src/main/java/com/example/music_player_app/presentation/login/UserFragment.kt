package com.example.music_player_app.presentation.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.music_player_app.databinding.FragmentUserBinding


class UserFragment(
    private val onLogout: () -> Unit
) : Fragment() {



    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefs = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = prefs?.getString("user_name", "Имя пользователя") ?: "Имя пользователя"
        binding.textViewUserName.text = userName

        binding.buttonLogout.setOnClickListener {
            prefs?.edit()?.putBoolean("is_logged_in", false)?.apply()
            onLogout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}