package com.example.notes.presentation.note_edit

import com.example.notes.domain.model.Tag

sealed interface NoteEditEvent {
    data class TitleChanged(val value: String) : NoteEditEvent
    data class ContentChanged(val value: String) : NoteEditEvent
    data class TagAdded(val tag: Tag) : NoteEditEvent
    data class TagRemoved(val tag: Tag) : NoteEditEvent
    data class ImagesAdded(val uris: List<String>) : NoteEditEvent
    data class ImageRemoved(val uri: String) : NoteEditEvent
    data object SaveClicked : NoteEditEvent
    data object BackClicked : NoteEditEvent
}