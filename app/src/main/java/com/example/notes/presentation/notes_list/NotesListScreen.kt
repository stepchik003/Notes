package com.example.notes.presentation.notes_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notes.R
import com.example.notes.presentation.components.NoteCard
import com.example.notes.presentation.components.NotesFilterBottomSheet
import com.example.notes.presentation.components.TagChip
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    onNoteClick: (Long) -> Unit,
    onAddNoteClick: () -> Unit,
    viewModel: NotesListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val isFilterActive = state.sortOrder != SortOrder.UPDATED_AT_DESC ||
            state.imageFilter != ImageFilter.ALL ||
            state.draftFilter != DraftFilter.ALL

    var isFilterSheetVisible by rememberSaveable { mutableStateOf(false) }

    if (isFilterSheetVisible) {
        NotesFilterBottomSheet(
            state = state,
            onEvent = viewModel::onEvent,
            onDismiss = { isFilterSheetVisible = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои Заметки") },
                actions = {
                    IconButton(onClick = { isFilterSheetVisible = true }) {
                        Icon(
                            painter = painterResource(R.drawable.filter),
                            contentDescription = "Фильтры",
                            tint = if (isFilterActive) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Добавить",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onEvent(NotesListEvent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Поиск заметок...") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Найти",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                singleLine = true
            )

            if (state.availableTags.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.availableTags) { tag ->
                        TagChip(
                            tag = tag,
                            isSelected = state.selectedTag == tag,
                            onClick = { viewModel.onEvent(NotesListEvent.TagSelected(tag)) }
                        )
                    }
                }
            }

            if (state.drafts.isEmpty() && state.notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Заметок пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                HorizontalDivider()
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.drafts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Черновики",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    bottom = 4.dp,
                                    top = 12.dp
                                )
                            )
                        }
                        items(state.drafts, key = { "draft_${it.id}" }) { draft ->
                            NoteCard(
                                note = draft,
                                onClick = { onNoteClick(draft.id) },
                                onDeleteClick = { viewModel.onEvent(NotesListEvent.DeleteNote(draft.id)) }
                            )
                        }
                    }

                    if (state.notes.isNotEmpty()) {
                        item {
                            if (state.drafts.isNotEmpty()) {
                                Text(
                                    text = "Сохраненные",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                )
                            }
                        }
                        items(state.notes, key = { "note_${it.id}" }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onDeleteClick = { viewModel.onEvent(NotesListEvent.DeleteNote(note.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}