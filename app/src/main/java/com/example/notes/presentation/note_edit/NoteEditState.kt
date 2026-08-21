package com.example.notes.presentation.note_edit

data class NoteEditState(
    val id: Long = 0L,
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isShouldNavigateBack: Boolean = false
)