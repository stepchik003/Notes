package com.example.notes.presentation.navigation

sealed class Screen(val route: String) {
    object NotesList : Screen("notes_list")
    object NoteEdit : Screen("note_edit?noteId={noteId}") {
        fun passNoteId(noteId: Long? = null): String {
            return if (noteId != null) "note_edit?noteId=$noteId" else "note_edit"
        }
    }
}