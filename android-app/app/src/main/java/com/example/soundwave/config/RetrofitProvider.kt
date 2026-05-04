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
import com.example.soundwave.data.remote.AuthRepository
import com.example.soundwave.data.remote.api.AuthApi
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private const val BASE_URL = "https://two026-0sh-team04-server.onrender.com"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val authHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofitAuth by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authApi by lazy { retrofitAuth.create(AuthApi::class.java) }

    private val authRepo by lazy { AuthRepository(authApi) }

    // Expose auth repository for callers that need to perform auth operations (e.g. startup refresh)
    val authRepository: AuthRepository
        get() = authRepo

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
        .authenticator(com.example.soundwave.data.remote.TokenAuthenticator(authRepo))
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
