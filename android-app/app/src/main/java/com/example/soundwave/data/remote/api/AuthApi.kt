package com.example.soundwave.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RefreshRequest(val refreshToken: String)
data class RefreshResponse(val accessToken: String, val user: Map<String, Any>)
data class LogoutRefreshRequest(val refreshToken: String)

interface AuthApi {
    @POST("api/auth/refresh")
    fun refresh(@Body req: RefreshRequest): Call<RefreshResponse>

    @POST("api/auth/logout")
    fun logout(@Body req: LogoutRefreshRequest): Call<Map<String, String>>
}
