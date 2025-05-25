package com.example.serializer


import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.io.StringReader
import java.io.StringWriter
import com.example.serializer.TrackSerializer

// Обертка для списка треков (нужна для XML)
@Root(name = "tracksWrapper", strict = false)
data class TracksWrapper(
    @field:ElementList(name = "tracks", inline = true, required = false)
    var tracks: List<Track> = emptyList()
)


// Сериализатор для списка треков
class XmlTrackSerializer : TrackSerializer<List<Track>> {
    private val persister = Persister()

    override fun serialize(data: List<Track>): String {
        val wrapper = TracksWrapper(data)
        val writer = StringWriter()
        persister.write(wrapper, writer)
        return writer.toString()
    }

    override fun deserialize(serialized: String): List<Track> {
        val wrapper = persister.read(TracksWrapper::class.java, StringReader(serialized))
        return wrapper.tracks
    }
}