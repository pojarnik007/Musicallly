package com.example.serializer


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonTrackListSerializer : TrackSerializer<List<Track>> {
    override fun serialize(data: List<Track>): String = Json.encodeToString(data)
    override fun deserialize(serialized: String): List<Track> = Json.decodeFromString(serialized)
}