package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {

    @GET("albums/{albumId}")
    suspend fun getAlbum(@Path("albumId") albumId: Int): Album
}
