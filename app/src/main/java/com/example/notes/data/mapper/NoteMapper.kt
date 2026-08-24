package com.example.notes.data.mapper

import com.example.notes.data.local.entity.NoteEntity
import com.example.notes.domain.model.Note

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    tags = tags,
    images = images,
    isDraft = isDraft,
    reminderTimestamp = reminderTimestamp
)

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    tags = tags,
    images = images,
    isDraft = isDraft,
    reminderTimestamp = reminderTimestamp
)