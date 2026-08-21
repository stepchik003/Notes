package com.example.notes.presentation.notes_list

sealed interface NotesListEvent {
    data class SearchQueryChanged(val query: String) : NotesListEvent
    data class TagSelected(val tag: String?) : NotesListEvent
    data class DeleteNote(val noteId: Long) : NotesListEvent
}