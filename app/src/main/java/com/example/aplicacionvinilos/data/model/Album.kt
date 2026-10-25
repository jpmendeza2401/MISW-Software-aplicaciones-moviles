package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("description") val description: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("recordLabel") val recordLabel: String,
    @SerializedName("performers") val performers: List<Performer> = emptyList(),
    @SerializedName("tracks") val tracks: List<Track> = emptyList(),
    @SerializedName("comments") val comments: List<Comment> = emptyList()
)
