package com.example.soundwave.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = TokenProvider.getToken()
        val request = if (!token.isNullOrEmpty()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else original

        return chain.proceed(request)
    }
}
