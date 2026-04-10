package com.example.soundwave.models

import com.example.soundwave.data.remote.UserResponse
import com.example.soundwave.data.remote.dto.user.UserDto

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    var avatarUrl: String?,
    val createdAt: String
) {
    companion object {
        fun fromDto(dto: UserDto): User {
            return User(
                id = dto.id,
                name = dto.username,
                email = dto.access?.email ?: "",
                password = "",
                avatarUrl = dto.avatarUrl,
                createdAt = dto.createdAt
            )
        }

        fun fromResponse(resp: UserResponse): User {
            return User(
                id = resp.id,
                name = resp.name,
                email = resp.email ?: "",
                password = resp.password,
                avatarUrl = resp.avatarUrl,
                createdAt = ""
            )
        }
    }

}