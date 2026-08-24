package com.example.notes.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Tag
import com.example.notes.domain.usecase.DeleteNoteUseCase
import com.example.notes.domain.usecase.GetNotesUseCase
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder
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
    private val _selectedTag = MutableStateFlow<Tag?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.UPDATED_AT_DESC)
    private val _imageFilter = MutableStateFlow(ImageFilter.ALL)
    private val _draftFilter = MutableStateFlow(DraftFilter.ALL)

    val uiState: StateFlow<NotesListState> = combine(
        _searchQuery,
        _selectedTag,
        _sortOrder,
        _imageFilter,
        _draftFilter
    ) { query, tag, sort, imageF, draftF ->
        FilterParams(query, tag, sort, imageF, draftF)
    }.flatMapLatest { params ->
        getNotesUseCase(
            query = params.query,
            selectedTag = params.tag,
            sortOrder = params.sort,
            imageFilter = params.imageF,
            draftFilter = params.draftF
        ).map { notesData ->
            NotesListState(
                drafts = notesData.drafts,
                notes = notesData.notes,
                availableTags = notesData.availableTags,
                selectedTag = params.tag,
                sortOrder = params.sort,
                imageFilter = params.imageF,
                draftFilter = params.draftF
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
            is NotesListEvent.SortOrderSelected -> _sortOrder.value = event.sortOrder
            is NotesListEvent.ImageFilterSelected -> _imageFilter.value = event.imageFilter
            is NotesListEvent.DraftFilterSelected -> _draftFilter.value = event.draftFilter
            is NotesListEvent.ResetFilters -> {
                _sortOrder.value = SortOrder.UPDATED_AT_DESC
                _imageFilter.value = ImageFilter.ALL
                _draftFilter.value = DraftFilter.ALL
                _selectedTag.value = null
            }
            is NotesListEvent.DeleteNote -> {
                viewModelScope.launch {
                    deleteNoteUseCase(event.noteId)
                }
            }
        }
    }

    private data class FilterParams(
        val query: String,
        val tag: Tag?,
        val sort: SortOrder,
        val imageF: ImageFilter,
        val draftF: DraftFilter
    )
}