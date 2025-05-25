package com.example.music_player_app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Int? = null,
    @SerialName("name")
    val title: String,
    val artist: String,
    val duration: Int
    // url убран, сам трек хранится на сервере
)