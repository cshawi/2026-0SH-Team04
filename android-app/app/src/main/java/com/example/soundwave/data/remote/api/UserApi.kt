package com.example.soundwave.data.remote.api

import com.example.soundwave.data.remote.dto.user.LoginRequestDto
import com.example.soundwave.data.remote.dto.user.LoginResponseDto
import com.example.soundwave.data.remote.dto.user.MessageResponseDto
import com.example.soundwave.data.remote.dto.user.SignupRequestDto
import com.example.soundwave.data.remote.dto.user.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/users")
    suspend fun signup(@Body request: SignupRequestDto): UserDto

    @GET("api/users/me")
    suspend fun getMe(): UserDto

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserDto

    @GET("api/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserDto

    @POST("api/users/logout")
    suspend fun logout(): MessageResponseDto
}
