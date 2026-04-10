package com.example.soundwave.data.remote

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator(private val authRepository: AuthRepository) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val newAccess = authRepository.refreshSync() ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var res: Response? = response
        var result = 0
        while (res != null) {
            result++
            res = res.priorResponse
        }
        return result
    }
}
