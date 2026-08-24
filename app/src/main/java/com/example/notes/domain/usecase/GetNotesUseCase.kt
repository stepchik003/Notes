package com.example.notes.domain.usecase

import com.example.notes.domain.model.NotesData
import com.example.notes.domain.model.Tag
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(
        query: String = "",
        selectedTag: Tag? = null,
        sortOrder: SortOrder = SortOrder.UPDATED_AT_DESC,
        imageFilter: ImageFilter = ImageFilter.ALL,
        draftFilter: DraftFilter = DraftFilter.ALL
    ): Flow<NotesData> {
        return repository.getNotes().map { allNotes ->
            val allTags = (allNotes.flatMap { it.tags })
                .distinctBy { it.name.trim().lowercase() }

            var filtered = allNotes.filter { note ->
                val matchesQuery = query.isBlank() ||
                        note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
                val matchesTag = selectedTag == null || note.tags.contains(selectedTag)

                val matchesImage = when (imageFilter) {
                    ImageFilter.ALL -> true
                    ImageFilter.ONLY_WITH_IMAGES -> note.images.isNotEmpty()
                    ImageFilter.WITHOUT_IMAGES -> note.images.isEmpty()
                }

                val matchesDraft = when (draftFilter) {
                    DraftFilter.ALL -> true
                    DraftFilter.ONLY_DRAFTS -> note.isDraft
                    DraftFilter.WITHOUT_DRAFTS -> !note.isDraft
                }

                matchesQuery && matchesTag && matchesImage && matchesDraft
            }

            filtered = when (sortOrder) {
                SortOrder.UPDATED_AT_DESC -> filtered.sortedByDescending { it.updatedAt }
                SortOrder.UPDATED_AT_ASC -> filtered.sortedBy { it.updatedAt }
                SortOrder.CREATED_AT_DESC -> filtered.sortedByDescending { it.createdAt }
                SortOrder.CREATED_AT_ASC -> filtered.sortedBy { it.createdAt }
                SortOrder.CONTENT_LENGTH_DESC -> filtered.sortedByDescending { it.content.length }
                SortOrder.CONTENT_LENGTH_ASC -> filtered.sortedBy { it.content.length }
                SortOrder.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
                SortOrder.TITLE_DESC -> filtered.sortedByDescending { it.title.lowercase() }
            }

            NotesData(
                drafts = filtered.filter { it.isDraft },
                notes = filtered.filter { !it.isDraft },
                availableTags = allTags
            )
        }
    }
}