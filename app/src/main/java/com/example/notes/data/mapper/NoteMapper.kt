package com.example.notes.data.mapper

import com.example.notes.data.local.entity.NoteEntity
import com.example.notes.domain.model.Note

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        imageUri = imageUri,
        isDraft = isDraft
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        imageUri = imageUri,
        isDraft = isDraft
    )
}