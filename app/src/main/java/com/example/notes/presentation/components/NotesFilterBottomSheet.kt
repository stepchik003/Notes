package com.example.notes.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.presentation.notes_list.NotesListEvent
import com.example.notes.presentation.notes_list.NotesListState
import com.example.notes.utils.DraftFilter
import com.example.notes.utils.ImageFilter
import com.example.notes.utils.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesFilterBottomSheet(
    state: NotesListState,
    onEvent: (NotesListEvent) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Сортировка и фильтры", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { onEvent(NotesListEvent.ResetFilters) }) {
                    Text("Сбросить")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Фотографии",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ImageFilter.entries) { option ->
                    FilterChip(
                        selected = state.imageFilter == option,
                        onClick = { onEvent(NotesListEvent.ImageFilterSelected(option)) },
                        label = { Text(text = option.displayName, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Черновики",
                style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DraftFilter.entries) { option ->
                    FilterChip(
                        selected = state.draftFilter == option,
                        onClick = { onEvent(NotesListEvent.DraftFilterSelected(option)) },
                        label = { Text(text = option.displayName, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Сортировка", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            SortOrder.entries.forEach { order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(NotesListEvent.SortOrderSelected(order)) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.sortOrder == order,
                        onClick = { onEvent(NotesListEvent.SortOrderSelected(order)) }
                    )
                    Text(text = order.displayName, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}