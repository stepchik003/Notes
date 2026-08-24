package com.example.notes.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<Tag> = emptyList(),
    val images: List<String> = emptyList(),
    val isDraft: Boolean = false,
    val reminderTimestamp: Long? = null
)
