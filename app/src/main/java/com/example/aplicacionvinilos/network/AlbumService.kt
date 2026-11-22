package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.AlbumRequest
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.CommentRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlbumService {

    @GET("albums/{albumId}")
    suspend fun getAlbum(@Path("albumId") albumId: Int): Album

    // HU09: publicar un comentario sobre un álbum (solo coleccionista).
    @POST("albums/{albumId}/comments")
    suspend fun addComment(
        @Path("albumId") albumId: Int,
        @Body comment: CommentRequest
    ): Comment

    // HU07: crear un álbum.
    @POST("albums")
    suspend fun createAlbum(@Body album: AlbumRequest): Album
}
