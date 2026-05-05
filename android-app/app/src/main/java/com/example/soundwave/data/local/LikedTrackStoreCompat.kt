package com.example.soundwave.data.local

class LikedTrackStoreCompat(private val room: LikedTrackStore? = null, private val file: LikedTrackFileStore? = null) {

    suspend fun getForUser(userId: String): List<LikedTrackEntity> {
        return try {
            if (room != null) {
                try {
                    room.getForUser(userId)
                } catch (_: Exception) {
                    // room exists but failed at runtime (e.g., missing generated Impl) -> fallback to file
                    file?.getForUser(userId) ?: emptyList()
                }
            } else {
                file?.getForUser(userId) ?: emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upsert(entity: LikedTrackEntity) {
        try {
            if (room != null) {
                room.upsert(entity)
            } else {
                file?.upsert(entity)
            }
        } catch (_: Exception) {
            try { file?.upsert(entity) } catch (_: Exception) {}
        }
    }

    suspend fun deleteByIdForUser(id: String, userId: String) {
        try {
            if (room != null) {
                room.deleteByIdForUser(id, userId)
            } else {
                file?.deleteByIdForUser(id, userId)
            }
        } catch (_: Exception) {
            try {
                file?.deleteByIdForUser(id, userId)
            } catch (_: Exception) {
                // ignore
            }
        }
    }
}
