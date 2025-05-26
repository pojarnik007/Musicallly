package com.example.music_player_app

import com.example.music_player_app.presentation.login.Fragment
import org.junit.Assert.*
import org.junit.Test

class AllTracksFragment : Fragment() {
    companion object {
        fun isAdmin(userName: String?): Boolean = userName == "admin"
    }
    // ... остальной код
}

class AllTracksFragmentTest {
    @Test
    fun testIsAdmin_AdminUser() {
        assertTrue(AllTracksFragment.isAdmin("admin"))
    }

    @Test
    fun testIsAdmin_NotAdminUser() {
        assertFalse(AllTracksFragment.isAdmin("user"))
        assertFalse(AllTracksFragment.isAdmin(null))
        assertFalse(AllTracksFragment.isAdmin(""))
    }
}