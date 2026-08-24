package com.example.notes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notes.domain.model.Tag

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val tags: List<Tag>,
    val images: List<String> = emptyList(),
    val isDraft: Boolean = false
)