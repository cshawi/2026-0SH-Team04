package com.example.soundwave.data.remote

import com.example.soundwave.data.remote.api.AuthApi
import com.example.soundwave.data.remote.api.RefreshRequest

class AuthRepository(private val authApi: AuthApi) {
    private val lock = Any()

    fun refreshSync(): String? {
        val currentAccess = TokenProvider.getToken()

        synchronized(lock) {
            val latestAccess = TokenProvider.getToken()
            if (latestAccess != null && latestAccess != currentAccess) {
                return latestAccess
            }

            val refresh = TokenProvider.getRefreshToken() ?: return null

            val call = authApi.refresh(RefreshRequest(refresh))
            return try {
                val resp = call.execute()
                if (!resp.isSuccessful) {
                    TokenProvider.clear()
                    null
                } else {
                    val body = resp.body() ?: run {
                        TokenProvider.clear()
                        return null
                    }
                    val newAccess = body.accessToken
                    TokenProvider.setToken(newAccess)
                    newAccess
                }
            } catch (e: Exception) {
                TokenProvider.clear()
                null
            }
        }
    }
}
