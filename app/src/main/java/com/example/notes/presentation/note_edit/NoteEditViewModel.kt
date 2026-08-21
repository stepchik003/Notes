package com.example.notes.presentation.note_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.usecase.GetNoteByIdUseCase
import com.example.notes.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteEditViewModel(
    private val noteId: Long?,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditState())
    val uiState: StateFlow<NoteEditState> = _uiState.asStateFlow()

    init {
        if (noteId != null && noteId > 0) {
            loadNote(noteId)
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = getNoteByIdUseCase(id)
            if (note != null) {
                _uiState.update {
                    it.copy(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        tags = note.tags,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: NoteEditEvent) {
        when (event) {
            is NoteEditEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.value) }
            }
            is NoteEditEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.value) }
            }
            is NoteEditEvent.SaveClicked -> {
                saveNote(isDraft = false)
            }
            is NoteEditEvent.BackClicked -> {
                val currentState = _uiState.value
                if (currentState.title.isNotBlank() || currentState.content.isNotBlank()) {
                    saveNote(isDraft = true)
                } else {
                    _uiState.update { it.copy(isShouldNavigateBack = true) }
                }
            }
        }
    }

    private fun saveNote(isDraft: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.title.isNotBlank() || currentState.content.isNotBlank()) {
                val note = Note(
                    id = currentState.id,
                    title = currentState.title.trim(),
                    content = currentState.content.trim(),
                    isDraft = isDraft,
                    tags = currentState.tags,
                    updatedAt = System.currentTimeMillis()
                )
                saveNoteUseCase(note)
            }
            _uiState.update { it.copy(isShouldNavigateBack = true) }
        }
    }
}