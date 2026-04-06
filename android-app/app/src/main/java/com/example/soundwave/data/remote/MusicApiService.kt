package com.example.soundwave.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// --- User-related DTOs ---
data class User(
    val _id: String,
    val username: String,
    val avatarUrl: String?,
    val access: Map<String, String>? = null // contains email, password is never returned
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val avatarUrl: String? = null
)

data class MessageResponse(val message: String?)

interface MusicApiService {
    @POST("/generate")
    suspend fun generateMusic(@Body request: GenerateMusicRequest): GenerateMusicResponse

    @GET("/generate/status")
    suspend fun checkStatus(@Query("taskId") taskId: String): GenerateStatusResponse

    @GET("/me")
    suspend fun getMe(): UserResponse

    // --- User routes ---
    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/users")
    suspend fun signup(@Body request: SignupRequest): User

    @GET("/users/me")
    suspend fun getCurrentUser(): User

    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") id: String): User

    @GET("/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): User

    @POST("/users/logout")
    suspend fun logout(): MessageResponse
}
