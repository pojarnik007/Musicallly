package com.example.music_player_app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val artist: String,
    val duration: Int,
    val localPath: String? = null // nullable, не сериализуется с сервера
)