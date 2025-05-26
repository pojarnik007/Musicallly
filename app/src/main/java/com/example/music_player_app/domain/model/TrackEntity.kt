package com.example.music_player_app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val artist: String,
    val duration: Int,
    val localPath: String // путь к локальному файлу
)