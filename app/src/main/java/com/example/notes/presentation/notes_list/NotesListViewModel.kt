package com.example.notes.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.usecase.DeleteNoteUseCase
import com.example.notes.domain.usecase.GetNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedTag = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NotesListState> = combine(_searchQuery, _selectedTag) { query, tag ->
        Pair(query, tag)
    }.flatMapLatest { (query, tag) ->
        getNotesUseCase(query, tag).map { notesData ->
            val allTags = (notesData.drafts + notesData.notes)
                .flatMap { it.tags }
                .distinct()

            NotesListState(
                drafts = notesData.drafts,
                notes = notesData.notes,
                searchQuery = query,
                availableTags = allTags,
                selectedTag = tag,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesListState(isLoading = true)
    )

    fun onEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.SearchQueryChanged -> _searchQuery.value = event.query
            is NotesListEvent.TagSelected -> {
                _selectedTag.value = if (_selectedTag.value == event.tag) null else event.tag
            }
            is NotesListEvent.DeleteNote -> {
                viewModelScope.launch {
                    deleteNoteUseCase(event.noteId)
                }
            }
        }
    }
}