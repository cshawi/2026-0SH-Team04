package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.dto.user.LoginRequestDto
import com.example.soundwave.data.remote.dto.user.LoginResponseDto
import com.example.soundwave.data.remote.dto.user.SignupRequestDto
import com.example.soundwave.data.remote.dto.user.UserDto
import com.example.soundwave.data.remote.TokenProvider

class UserRepository {
    private val api = RetrofitProvider.userApi

    suspend fun login(email: String, password: String): Result<LoginResponseDto> = runCatching {
        val resp = api.login(LoginRequestDto(email, password))
        TokenProvider.setToken(resp.token)
        resp
    }

    suspend fun signup(username: String, email: String, password: String, avatarUrl: String? = null): Result<UserDto> = runCatching {
        val req = SignupRequestDto(username, email, password, avatarUrl)
        api.signup(req)
    }

    suspend fun getMe(): Result<UserDto> = runCatching {
    api.getMe()
    }

    suspend fun logout(): Result<Unit> = runCatching {
    api.logout()
    TokenProvider.clear()
    }
}
