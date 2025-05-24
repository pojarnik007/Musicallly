package com.example.music_player_app.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val url: String,      // Ссылка на mp3 или локальный путь
    val duration: Int     // Длительность в секундах
)