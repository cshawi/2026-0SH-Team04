package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.dto.user.LoginRequestDto
import com.example.soundwave.data.remote.dto.user.LoginResponseDto
import com.example.soundwave.data.remote.dto.user.SignupRequestDto
import com.example.soundwave.data.remote.dto.user.UserDto
import com.example.soundwave.data.remote.TokenProvider
import retrofit2.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val api = RetrofitProvider.userApi

    suspend fun login(email: String, password: String): Result<LoginResponseDto> = runCatching {
        val resp = api.login(LoginRequestDto(email, password))
        TokenProvider.setToken(resp.accessToken)
        resp.refreshToken?.let { TokenProvider.setRefreshToken(it) }
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

    suspend fun deleteAccount(): Result<Unit> = runCatching {
        api.deleteMe()
        TokenProvider.clear()
    }

    suspend fun tryRestoreSession(): Result<UserDto?> = runCatching {
        withContext(Dispatchers.IO) {
            val token = TokenProvider.getToken() ?: return@withContext null

            try {
                // Try to fetch the current user with the existing token
                api.getMe()
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    // token invalid -> attempt refresh synchronously
                    val newAccess = RetrofitProvider.authRepository.refreshSync()
                    if (newAccess != null) {
                        // retry once with refreshed token
                        api.getMe()
                    } else {
                        // refresh failed -> clear stored tokens
                        TokenProvider.clear()
                        null
                    }
                } else {
                    throw e
                }
            }
        }
    }
}
