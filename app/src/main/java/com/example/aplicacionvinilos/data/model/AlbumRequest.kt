package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

// HU07: cuerpo de POST /albums.
data class AlbumRequest(
    @SerializedName("name") val name: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("description") val description: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("recordLabel") val recordLabel: String
)
