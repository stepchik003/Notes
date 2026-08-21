package com.example.notes.presentation.notes_list

import com.example.notes.domain.model.Note

data class NotesListState(
    val drafts: List<Note> = emptyList(),
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val isLoading: Boolean = false
)