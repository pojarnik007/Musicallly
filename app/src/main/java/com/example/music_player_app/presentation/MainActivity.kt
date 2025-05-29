package com.example.music_player_app.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.music_player_app.R
import com.example.music_player_app.databinding.ActivityMainBinding
import com.example.music_player_app.presentation.alltracks.AllTracksFragment
import com.example.music_player_app.presentation.login.LoginFragment
import com.example.music_player_app.presentation.login.RegisterFragment
import com.example.music_player_app.presentation.player.PlayerFragment
import com.example.music_player_app.presentation.login.UserFragment
import com.example.music_player_app.presentation.player.MiniPlayerFragment
import com.example.music_player_app.presentation.player.PlayerViewModel
import kotlinx.coroutines.awaitAll

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            openFragment(AllTracksFragment())
        } else {
            openLogin()
        }

        // Динамически добавить MiniPlayerFragment, если его нет
        if (supportFragmentManager.findFragmentById(R.id.fragmentMiniPlayer) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentMiniPlayer, MiniPlayerFragment())
                .commit()
        }

        playerViewModel.currentTrack.observe(this) { updateMiniPlayerVisibility() }
        supportFragmentManager.addOnBackStackChangedListener { updateMiniPlayerVisibility() }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
            updateMiniPlayerVisibility()
            if (current is LoginFragment || current is RegisterFragment) {
                return@setOnItemSelectedListener false
            }
            when (item.itemId) {
                R.id.menu_all_tracks -> openFragment(AllTracksFragment())
                R.id.menu_user -> openFragment(UserFragment(onLogout = {
                    openLogin()
                }))
            }
            true
        }
    }


    private fun openLogin() {
        openFragment(LoginFragment(
            onLoginSuccess = {
                getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().putBoolean("is_logged_in", true).apply()
                openFragment(AllTracksFragment())
            },
            onGoToRegister = {
                openFragment(RegisterFragment(
                    onRegisterSuccess = {
                        openLogin()
                    }
                ))
            }
        ))
    }


    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        updateMiniPlayerVisibility()
    }

    private fun updateMiniPlayerVisibility() {
        val miniPlayer = findViewById<View>(R.id.fragmentMiniPlayer)
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val isPlayerVisible = current is PlayerFragment
        val track = playerViewModel.currentTrack.value
        miniPlayer?.visibility = if (track != null && !isPlayerVisible) View.VISIBLE else View.GONE
    }

}