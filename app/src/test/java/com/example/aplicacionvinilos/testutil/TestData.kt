package com.example.aplicacionvinilos.testutil

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.Performer
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.network.AlbumService
import com.example.aplicacionvinilos.network.ApiService

fun performer(id: Int = 1, name: String = "Queen") =
    Performer(id, name, "img", "desc", null)

fun album(
    id: Int = 1,
    name: String = "A Night at the Opera",
    performers: List<Performer> = listOf(performer()),
    tracks: List<Track> = listOf(Track(1, "Bohemian Rhapsody", "05:55")),
    comments: List<Comment> = listOf(Comment(1, "Excelente", 5))
) = Album(
    id = id,
    name = name,
    cover = "cover",
    releaseDate = "1975-11-21T00:00:00.000Z",
    description = "Descripción",
    genre = "Rock",
    recordLabel = "EMI",
    performers = performers,
    tracks = tracks,
    comments = comments
)

/** Fake de ApiService (HU01): ejecuta [block] en cada llamada a getAlbums. */
class FakeApiService(private val block: suspend () -> List<Album>) : ApiService {
    override suspend fun getAlbumBand(albumId: Int, bandId: Int): Band =
        throw UnsupportedOperationException("No se usa en HU01")

    override suspend fun getAlbums(): List<Album> = block()
}

/** Fake de AlbumService (HU02). */
class FakeAlbumService(private val block: suspend (Int) -> Album) : AlbumService {
    override suspend fun getAlbum(albumId: Int): Album = block(albumId)
}
