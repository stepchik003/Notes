package com.example.notes.domain.usecase

import com.example.notes.domain.model.NotesData
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String = "", selectedTag: String? = null): Flow<NotesData> {
        return repository.getNotes().map {allNotes ->
            val filtered = allNotes.filter { note ->
                val matchesQuery = query.isBlank() ||
                        note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
                val matchesTag = selectedTag == null || note.tags.contains(selectedTag)
                matchesQuery && matchesTag
            }

            NotesData(
                drafts = filtered.filter { it.isDraft },
                notes = filtered.filter { !it.isDraft }
            )
        }
    }
}