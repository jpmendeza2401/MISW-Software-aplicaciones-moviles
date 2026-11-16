package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

// HU09: cuerpo de POST /albums/{albumId}/comments
// { "description": "...", "rating": 4, "collector": { "id": 1 } }
data class CommentRequest(
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("collector") val collector: CollectorRef
)

data class CollectorRef(
    @SerializedName("id") val id: Int
)
