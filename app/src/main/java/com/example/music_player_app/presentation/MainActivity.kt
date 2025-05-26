package com.example.music_player_app.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.music_player_app.R
import com.example.music_player_app.databinding.ActivityMainBinding
import com.example.music_player_app.presentation.alltracks.AllTracksFragment
import com.example.music_player_app.presentation.login.LoginFragment
import com.example.music_player_app.presentation.login.RegisterFragment
import com.example.music_player_app.presentation.player.PlayerFragment
import com.example.music_player_app.presentation.login.UserFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (current is LoginFragment || current is RegisterFragment) {
                return@setOnItemSelectedListener false
            }
            when (item.itemId) {
                R.id.menu_all_tracks -> openFragment(AllTracksFragment())
                R.id.menu_player -> openFragment(PlayerFragment())
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
        // Скрываем или показываем нижний бар
        binding.bottomNavigation.visibility =
            if (fragment is LoginFragment || fragment is RegisterFragment) View.GONE else View.VISIBLE
    }
}