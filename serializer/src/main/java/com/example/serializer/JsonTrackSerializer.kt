package com.example.serializer


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonTrackSerializer : TrackSerializer<Track> {
    override fun serialize(data: Track): String = Json.encodeToString(data)
    override fun deserialize(serialized: String): Track = Json.decodeFromString(serialized)
}