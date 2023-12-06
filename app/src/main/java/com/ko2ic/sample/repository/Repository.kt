package com.ko2ic.sample.repository

import com.ko2ic.sample.model.ShortMovieDto
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun fetch(): Flow<List<ShortMovieDto>>
}
