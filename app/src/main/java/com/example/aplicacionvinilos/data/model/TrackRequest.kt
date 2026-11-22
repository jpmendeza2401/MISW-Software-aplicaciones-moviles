package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

// HU08: cuerpo de POST /albums/{albumId}/tracks. duration con formato m:ss.
data class TrackRequest(
    @SerializedName("name") val name: String,
    @SerializedName("duration") val duration: String
)
