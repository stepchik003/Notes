package com.example.notes.data.local.db

import androidx.room.TypeConverter
import com.example.notes.domain.model.Tag
import org.json.JSONArray
import org.json.JSONObject

class TagsConverter {

    @TypeConverter
    fun fromTagsList(tags: List<Tag>): String {
        val jsonArray = JSONArray()
        tags.forEach { tag ->
            val jsonObject = JSONObject().apply {
                put("name", tag.name)
                put("colorHex", tag.colorHex)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toTagsList(data: String): List<Tag> {
        if (data.isBlank()) return emptyList()
        val tags = mutableListOf<Tag>()
        try {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                tags.add(
                    Tag(
                        name = item.getString("name"),
                        colorHex = item.optString("colorHex", "#6750A4")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tags
    }
}