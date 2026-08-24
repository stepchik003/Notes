package com.example.notes.presentation.note_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.receiver.ReminderScheduler
import com.example.notes.domain.model.Note
import com.example.notes.domain.usecase.GetNoteByIdUseCase
import com.example.notes.domain.usecase.GetNotesUseCase
import com.example.notes.domain.usecase.SaveNoteUseCase
import com.example.notes.utils.DEFAULT_TAGS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteEditViewModel(
    noteId: Long?,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditState())
    val uiState: StateFlow<NoteEditState> = _uiState.asStateFlow()

    private var initialNote: Note? = null

    init {
        loadAvailableTags()
        if (noteId != null && noteId > 0) {
            loadNote(noteId)
        }
    }

    private fun loadAvailableTags() {
        viewModelScope.launch {
            getNotesUseCase().collect { notesData ->
                val allNotes = notesData.drafts + notesData.notes
                val savedTags = allNotes.flatMap { it.tags }
                val combinedTags =
                    (DEFAULT_TAGS + savedTags).distinctBy { it.name.trim().lowercase() }
                _uiState.update { it.copy(availableTags = combinedTags) }
            }
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = getNoteByIdUseCase(id)
            if (note != null) {
                initialNote = note
                _uiState.update {
                    it.copy(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        tags = note.tags,
                        isDraft = note.isDraft,
                        images = note.images,
                        reminderTimestamp = note.reminderTimestamp,
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

            is NoteEditEvent.TagAdded -> {
                _uiState.update { state ->
                    if (state.tags.none { it.name == event.tag.name }) {
                        state.copy(tags = state.tags + event.tag)
                    } else state
                }
            }

            is NoteEditEvent.TagRemoved -> {
                _uiState.update { state ->
                    state.copy(tags = state.tags.filter { it != event.tag })
                }
            }

            is NoteEditEvent.ImagesAdded -> {
                _uiState.update { it.copy(images = it.images + event.uris) }
            }

            is NoteEditEvent.ImageRemoved -> {
                _uiState.update { it.copy(images = it.images - event.uri) }
            }

            is NoteEditEvent.SaveClicked -> {
                saveNote(isDraft = false)
            }

            is NoteEditEvent.BackClicked -> {
                val currentState = _uiState.value
                if (hasChanges(currentState)) {
                    saveNote(isDraft = true)
                } else {
                    _uiState.update { it.copy(isShouldNavigateBack = true) }
                }
            }
            is NoteEditEvent.ReminderChanged -> {
                _uiState.update { it.copy(reminderTimestamp = event.timestamp) }
            }
        }
    }

    private fun isNoteNotEmpty(state: NoteEditState): Boolean {
        return state.title.isNotBlank() || state.content.isNotBlank() || state.images.isNotEmpty()
    }

    private fun hasChanges(state: NoteEditState): Boolean {
        val initial = initialNote
        return if (initial != null) {
            state.title.trim() != initial.title ||
                    state.content.trim() != initial.content ||
                    state.tags != initial.tags ||
                    state.images != initial.images
        } else {
            isNoteNotEmpty(state)
        }
    }

    private fun saveNote(isDraft: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (isNoteNotEmpty(currentState)) {
                val note = Note(
                    id = currentState.id,
                    title = currentState.title.trim(),
                    content = currentState.content.trim(),
                    isDraft = isDraft,
                    tags = currentState.tags,
                    images = currentState.images,
                    updatedAt = System.currentTimeMillis(),
                    reminderTimestamp = currentState.reminderTimestamp
                )
                saveNoteUseCase(note)
                if (currentState.reminderTimestamp != null && currentState.reminderTimestamp > System.currentTimeMillis()) {
                    reminderScheduler.schedule(
                        noteId = note.id,
                        title = note.title,
                        content = note.content,
                        timeInMillis = currentState.reminderTimestamp
                    )
                } else if (currentState.reminderTimestamp == null) {
                    reminderScheduler.cancel(note.id)
                }
            }
            _uiState.update { it.copy(isShouldNavigateBack = true) }
        }
    }
}