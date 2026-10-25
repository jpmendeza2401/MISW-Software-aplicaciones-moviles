package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

// Mismos campos que en banda; se mantiene como una clase independiente porque la estructura de datos del álbum define los por separado.
data class Performer(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("description") val description: String,
    @SerializedName("creationDate") val creationDate: String?
)
