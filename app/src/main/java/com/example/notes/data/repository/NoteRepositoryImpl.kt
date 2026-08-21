package com.example.notes.data.repository

import com.example.notes.data.local.db.NoteDao
import com.example.notes.data.mapper.toDomain
import com.example.notes.data.mapper.toEntity
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)?.toDomain()
    }

    override suspend fun saveNote(note: Note) {
        noteDao.upsertNote(note.toEntity())
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteNoteById(id)
    }
}