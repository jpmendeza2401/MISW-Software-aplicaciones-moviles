package com.example.aplicacionvinilos.testutil

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.AlbumRequest
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.data.model.CollectorAlbum
import com.example.aplicacionvinilos.data.model.CollectorAlbumRequest
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.CommentRequest
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

    override suspend fun getCollectors(): List<com.example.aplicacionvinilos.data.model.Collector> =
        throw UnsupportedOperationException()

    override suspend fun getCollector(collectorId: Int): com.example.aplicacionvinilos.data.model.Collector =
        throw UnsupportedOperationException()

    override suspend fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        body: CollectorAlbumRequest
    ): CollectorAlbum = throw UnsupportedOperationException()
}

/** Fake de ApiService (HU11): [onAssociate] responde a POST /collectors/{cid}/albums/{aid}. */
class FakeCollectorApiService(
    private val onAssociate: suspend (Int, Int, CollectorAlbumRequest) -> CollectorAlbum = { _, _, b ->
        CollectorAlbum(1, b.price, b.status)
    }
) : ApiService {
    var associateCalls = 0
    var postedCollectorId: Int? = null
    var postedAlbumId: Int? = null
    var postedBody: CollectorAlbumRequest? = null

    override suspend fun getAlbumBand(albumId: Int, bandId: Int): Band =
        throw UnsupportedOperationException()

    override suspend fun getAlbums(): List<Album> = throw UnsupportedOperationException()

    override suspend fun getCollectors(): List<com.example.aplicacionvinilos.data.model.Collector> =
        throw UnsupportedOperationException()

    override suspend fun getCollector(collectorId: Int): com.example.aplicacionvinilos.data.model.Collector =
        throw UnsupportedOperationException()

    override suspend fun addAlbumToCollector(
        collectorId: Int,
        albumId: Int,
        body: CollectorAlbumRequest
    ): CollectorAlbum {
        associateCalls++
        postedCollectorId = collectorId
        postedAlbumId = albumId
        postedBody = body
        return onAssociate(collectorId, albumId, body)
    }
}

/** Fake de AlbumService (HU02, HU07 y HU09). [onComment] y [onCreate] responden a los POST. */
class FakeAlbumService(
    private val onComment: suspend (Int, CommentRequest) -> Comment = { _, req ->
        Comment(99, req.description, req.rating)
    },
    private val onCreate: suspend (AlbumRequest) -> Album = { req ->
        album(100, req.name)
    },
    private val block: suspend (Int) -> Album
) : AlbumService {
    var postedAlbumId: Int? = null
    var postedComment: CommentRequest? = null
    var postedAlbum: AlbumRequest? = null

    override suspend fun getAlbum(albumId: Int): Album = block(albumId)

    override suspend fun addComment(albumId: Int, comment: CommentRequest): Comment {
        postedAlbumId = albumId
        postedComment = comment
        return onComment(albumId, comment)
    }

    override suspend fun createAlbum(album: AlbumRequest): Album {
        postedAlbum = album
        return onCreate(album)
    }
}
