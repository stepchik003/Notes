package com.example.notes.presentation.notes_list

import com.example.notes.domain.model.Note
import com.example.notes.domain.model.Tag
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder

data class NotesListState(
    val drafts: List<Note> = emptyList(),
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val availableTags: List<Tag> = emptyList(),
    val selectedTag: Tag? = null,
    val isLoading: Boolean = false,
    val sortOrder: SortOrder = SortOrder.UPDATED_AT_DESC,
    val imageFilter: ImageFilter = ImageFilter.ALL,
    val draftFilter: DraftFilter = DraftFilter.ALL
)