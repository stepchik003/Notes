package com.example.notes.domain.usecase

import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank() && note.content.isBlank()) return

        val updatedNote = note.copy(
            updatedAt = System.currentTimeMillis()
        )
        repository.saveNote(updatedNote)
    }
}