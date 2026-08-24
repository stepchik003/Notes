package com.example.notes.domain.model

data class NotesData(
    val drafts: List<Note> = emptyList(),
    val notes: List<Note> = emptyList(),
    val availableTags: List<Tag> = emptyList()
)
