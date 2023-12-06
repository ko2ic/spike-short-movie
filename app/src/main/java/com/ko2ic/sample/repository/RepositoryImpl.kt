package com.ko2ic.sample.repository

import android.util.Log
import com.ko2ic.sample.model.ShortMovieDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.CancellationException
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {

    private val api = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://old.reddit.com/")
        .build()
        .create(RedditHttpClient::class.java)

    override fun fetch(): Flow<List<ShortMovieDto>> = flow {
        try {
            val response = api.find()
            val videoData = response
                .data
                ?.posts
                ?.map { post ->
                    val video = post.data?.secureMedia?.video
                    val width = video?.width
                    val height = video?.height
                    val aspectRatio = if (width != null && height != null) {
                        width.toFloat() / height.toFloat()
                    } else {
                        null
                    }
                    ShortMovieDto(
                        id = post.data?.id.orEmpty(),
                        mediaUri = video?.hlsUrl.orEmpty(),
                        previewImageUri = post.data?.preview?.images?.firstOrNull()?.source?.url.orEmpty(),
                        aspectRatio = aspectRatio
                    )
                }
                ?.filter { videoData ->
                    videoData.id.isNotBlank()
                        && videoData.mediaUri.isNotBlank()
                        && videoData.previewImageUri.isNotBlank()
                }
                .orEmpty()

            emit(videoData)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable
            Log.d("asdf", "Error", throwable)
        }
    }

    private interface RedditHttpClient {

        @GET("/r/tiktokcringe/{sort}.json?raw_json=1")
        suspend fun find(
            @Path("sort") sort: String? = "top",
            @Query("t") top: String? = "today"
        ): RedditResponse
    }

    @JsonClass(generateAdapter = true)
    internal data class RedditResponse(
        val data: Data1?
    ) {

        @JsonClass(generateAdapter = true)
        data class Data1(
            @Json(name = "children")
            val posts: List<Post>?
        ) {

            @JsonClass(generateAdapter = true)
            data class Post(
                val data: Data2?
            ) {

                @JsonClass(generateAdapter = true)
                data class Data2(
                    val id: String,
                    @Json(name = "secure_media")
                    val secureMedia: SecureMedia?,
                    val preview: Preview?
                ) {

                    @JsonClass(generateAdapter = true)
                    data class SecureMedia(
                        @Json(name = "reddit_video")
                        val video: Video?
                    ) {

                        @JsonClass(generateAdapter = true)
                        data class Video(
                            val width: Int?,
                            val height: Int?,
                            val duration: Int?,
                            @Json(name = "hls_url")
                            val hlsUrl: String?,
                            @Json(name = "dash_url")
                            val dashUrl: String?
                        )
                    }

                    @JsonClass(generateAdapter = true)
                    data class Preview(
                        val images: List<Image>?
                    ) {

                        @JsonClass(generateAdapter = true)
                        data class Image(
                            val source: Source?
                        ) {

                            @JsonClass(generateAdapter = true)
                            data class Source(
                                val url: String?,
                                val width: Int?,
                                val height: Int?
                            )
                        }
                    }
                }
            }
        }
    }
}
