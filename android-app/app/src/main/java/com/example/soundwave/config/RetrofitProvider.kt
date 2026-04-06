package com.example.soundwave.config

import com.example.soundwave.BuildConfig
import com.example.soundwave.data.remote.MusicApiService
import com.example.soundwave.data.remote.api.UserApi
import com.example.soundwave.data.remote.api.PlaylistApi
import com.example.soundwave.data.remote.api.TrackApi
import com.example.soundwave.data.remote.api.PlayApi
import com.example.soundwave.data.remote.api.JobApi
import okhttp3.OkHttpClient
import com.example.soundwave.data.remote.AuthInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: MusicApiService by lazy { retrofit.create(MusicApiService::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val playlistApi: PlaylistApi by lazy { retrofit.create(PlaylistApi::class.java) }
    val trackApi: TrackApi by lazy { retrofit.create(TrackApi::class.java) }
    val playApi: PlayApi by lazy { retrofit.create(PlayApi::class.java) }
    val jobApi: JobApi by lazy { retrofit.create(JobApi::class.java) }
}
