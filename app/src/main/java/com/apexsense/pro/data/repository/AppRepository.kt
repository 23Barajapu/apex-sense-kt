package com.apexsense.pro.data.repository

import com.apexsense.pro.data.remote.SupabaseClientProvider
import com.apexsense.pro.domain.model.*
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository {
    private val client = SupabaseClientProvider.client

    // --- Devices / Sensitivity ---
    suspend fun getRecommendedSensitivity(width: Int, height: Int): Device? = withContext(Dispatchers.IO) {
        try {
            client.from("devices")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("screen_width", width)
                        eq("screen_height", height)
                    }
                }
                .decodeSingleOrNull<Device>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Feedback ---
    suspend fun sendFeedback(feedback: Feedback): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("feedback").insert(feedback)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Game Library ---
    suspend fun getGames(): List<Game> = withContext(Dispatchers.IO) {
        try {
            client.from("game_library").select().decodeList<Game>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addGame(game: Game): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("game_library").insert(game)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Hardware History ---
    suspend fun getHardwareHistory(): List<HardwareHistory> = withContext(Dispatchers.IO) {
        try {
            client.from("hardware_history").select().decodeList<HardwareHistory>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveHardwareHistory(history: HardwareHistory): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("hardware_history").insert(history)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
