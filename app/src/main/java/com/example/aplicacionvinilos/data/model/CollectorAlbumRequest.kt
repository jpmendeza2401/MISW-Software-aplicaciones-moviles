package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

// HU11: cuerpo de POST /collectors/{collectorId}/albums/{albumId}.
data class CollectorAlbumRequest(
    @SerializedName("price") val price: Int,
    @SerializedName("status") val status: String
)
