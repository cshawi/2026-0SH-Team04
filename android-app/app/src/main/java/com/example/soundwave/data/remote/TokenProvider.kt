package com.example.soundwave.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenProvider {
    private const val PREF_FILE = "secure_prefs"
    private const val KEY_TOKEN = "auth_token"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
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
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? = prefs?.getString(KEY_TOKEN, null)

    fun clear() {
        prefs?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}
