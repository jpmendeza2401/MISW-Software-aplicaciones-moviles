package com.example.aplicacionvinilos.data.model

import com.google.gson.annotations.SerializedName

data class Band(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("description") val description: String,
    @SerializedName("creationDate") val creationDate: String?
)
