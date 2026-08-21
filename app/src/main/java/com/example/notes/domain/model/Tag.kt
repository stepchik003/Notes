package com.example.notes.domain.model

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

data class Tag(
    val name: String,
    val colorHex: String = "#6750A4"
)

fun Tag.getComposeColor(): Color {
    return try {
        Color(colorHex.toColorInt())
    } catch (e: Exception) {
        Color(0xFF6750A4)
    }
}