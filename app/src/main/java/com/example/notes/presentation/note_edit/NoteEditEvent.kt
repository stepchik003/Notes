package com.example.notes.presentation.note_edit

sealed interface NoteEditEvent {
    data class TitleChanged(val value: String) : NoteEditEvent
    data class ContentChanged(val value: String) : NoteEditEvent
    data object SaveClicked : NoteEditEvent
    data object BackClicked : NoteEditEvent
}