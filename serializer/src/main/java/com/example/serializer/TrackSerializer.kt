package com.example.serializer

interface TrackSerializer<T> {
    fun serialize(data: T): String
    fun deserialize(serialized: String): T
}