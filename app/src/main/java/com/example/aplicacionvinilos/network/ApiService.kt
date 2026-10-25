package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Band
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("albums/{albumId}/bands/{bandId}")
    suspend fun getAlbumBand(
        @Path("albumId") albumId: Int,
        @Path("bandId") bandId: Int
    ): Band
}
