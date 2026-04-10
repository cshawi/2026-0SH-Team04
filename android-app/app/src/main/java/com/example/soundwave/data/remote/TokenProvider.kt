@file:Suppress("DEPRECATION")

package com.example.soundwave.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenProvider {
    private const val PREF_FILE = "secure_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_REFRESH = "refresh_token"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        try {
            // Use the DEFAULT_MASTER_KEY_ALIAS explicit constructor to avoid deprecated overloads
            val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            prefs = EncryptedSharedPreferences.create(
                context,
                PREF_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences if EncryptedSharedPreferences not available
            prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        }
    }

    fun setToken(token: String?) {
        prefs?.edit(commit = false) {
            putString(KEY_TOKEN, token)
        }
    }

    fun setRefreshToken(refreshToken: String?) {
        prefs?.edit(commit = false) {
            putString(KEY_REFRESH, refreshToken)
        }
    }

    fun getToken(): String? = prefs?.getString(KEY_TOKEN, null)

    fun getRefreshToken(): String? = prefs?.getString(KEY_REFRESH, null)

    fun clear() {
        prefs?.edit(commit = false) {
            remove(KEY_TOKEN)
            remove(KEY_REFRESH)
        }
    }
}
