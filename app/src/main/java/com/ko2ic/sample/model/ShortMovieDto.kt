package com.ko2ic.sample.model

data class ShortMovieDto(
    val id: String,
    val mediaUri: String,
    val previewImageUri: String,
    val aspectRatio: Float? = null
)
