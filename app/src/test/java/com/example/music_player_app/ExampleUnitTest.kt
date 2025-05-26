package com.example.music_player_app

import org.junit.Assert.*
import org.junit.Test

class TimeUtilTest {
    @Test
    fun testFormatMillis() {
        assertEquals("0:00", TimeUtil.formatMillis(0))
        assertEquals("1:05", TimeUtil.formatMillis(65_000))
        assertEquals("10:59", TimeUtil.formatMillis(659_000))
    }
}

object TimeUtil {
    fun formatMillis(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}