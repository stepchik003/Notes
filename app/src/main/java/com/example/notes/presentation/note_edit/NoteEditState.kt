package com.example.notes.presentation.note_edit

import com.example.notes.domain.model.Tag

data class NoteEditState(
    val id: Long = 0L,
    val title: String = "",
    val content: String = "",
    val tags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val images: List<String> = emptyList(),
    val isDraft: Boolean = true,
    val isLoading: Boolean = false,
    val isShouldNavigateBack: Boolean = false
)