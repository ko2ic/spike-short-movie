package com.ko2ic.sample.di

import android.content.Context
import coil.Coil
import coil.ImageLoader
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.ko2ic.sample.repository.Repository
import com.ko2ic.sample.repository.RepositoryImpl
import com.ko2ic.sample.ui.view.player.ShortMoviePlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return Coil.imageLoader(context)
    }
}

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): Player {
        val exoPlayer = ExoPlayer.Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
        return exoPlayer
    }

    @Provides
    fun provideShortMoviePlayer(exoPlayer: Player): ShortMoviePlayer {
        return ShortMoviePlayer(exoPlayer)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractModule {

    @Singleton
    @Binds
    abstract fun bindRepository(repository: RepositoryImpl): Repository

}