package com.apexsense.pro.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Int? = null,
    val device_model: String = "",
    val screen_width: Int = 0,
    val screen_height: Int = 0,
    val recommended_dpi: Int = 440,
    val gen_sens: Double = 0.0,
    val red_dot_sens: Double = 0.0,
    val scope_2x_sens: Double = 0.0,
    val scope_4x_sens: Double = 0.0,
    val sniper_sens: Double = 0.0,
    val free_look_sens: Double = 0.0
)

@Serializable
data class Feedback(
    val id: Int? = null,
    val device_id: Int,
    val rating: String, // Upvote/Downvote
    val sensation: String, // Licin/Pas/Kesat
    val created_at: String? = null
)

@Serializable
data class Game(
    val id: Int? = null,
    val name: String,
    val icon_url: String,
    val is_favorite: Boolean = false,
    val package_name: String? = null
)

@Serializable
data class HardwareHistory(
    val id: Int? = null,
    val device_model: String,
    val cpu_usage: Int,
    val gpu_usage: Int = 0,
    val fps: Int = 60,
    val temp: Double,
    val created_at: String? = null
)
