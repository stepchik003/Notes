package com.example.notes.presentation.notes_list

import com.example.notes.domain.model.Tag
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder

sealed interface NotesListEvent {
    data class SearchQueryChanged(val query: String) : NotesListEvent
    data class TagSelected(val tag: Tag?) : NotesListEvent
    data class SortOrderSelected(val sortOrder: SortOrder) : NotesListEvent
    data class ImageFilterSelected(val imageFilter: ImageFilter) : NotesListEvent
    data class DraftFilterSelected(val draftFilter: DraftFilter) : NotesListEvent
    data class DeleteNote(val noteId: Long) : NotesListEvent
    data object ResetFilters : NotesListEvent
}