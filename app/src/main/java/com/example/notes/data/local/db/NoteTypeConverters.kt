package com.example.notes.data.local.db

import androidx.room.TypeConverter

class NoteTypeConverters {

    @TypeConverter
    fun fromTagsList(tags: List<String>): String {
        return tags.joinToString(separator = "|||")
    }

    @TypeConverter
    fun toTagsList(tagsString: String): List<String> {
        if (tagsString.isBlank()) return emptyList()
        return tagsString.split("|||")
    }
}