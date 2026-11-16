package com.example.aplicacionvinilos.network

import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.data.model.Collector
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("albums/{albumId}/bands/{bandId}")
    suspend fun getAlbumBand(
        @Path("albumId") albumId: Int,
        @Path("bandId") bandId: Int
    ): Band

    // HU01: catálogo de álbumes. Cada álbum trae sus performers (artistas).
    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("collectors")
    suspend fun getCollectors(): List<Collector>

    @GET("collectors/{collectorId}")
    suspend fun getCollector(
        @Path("collectorId") collectorId: Int
    ): Collector
}
