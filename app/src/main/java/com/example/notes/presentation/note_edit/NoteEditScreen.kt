package com.example.notes.presentation.note_edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.notes.R
import com.example.notes.presentation.components.AddTagDialog
import com.example.notes.presentation.components.FullScreenImageGalleryDialog
import com.example.notes.presentation.components.TagChip
import com.example.notes.utils.getOrSaveImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: NoteEditViewModel = koinViewModel { parametersOf(noteId) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isAddTagDialogVisible by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val internalPaths = uris.mapNotNull { uri ->
                getOrSaveImage(context, uri)
            }
            viewModel.onEvent(NoteEditEvent.ImagesAdded(internalPaths))
        }
    }

    LaunchedEffect(state.isShouldNavigateBack) {
        if (state.isShouldNavigateBack) {
            onNavigateBack()
        }
    }

    selectedImageIndex?.let { index ->
        FullScreenImageGalleryDialog(
            images = state.images,
            initialIndex = index,
            onDismiss = { selectedImageIndex = null }
        )
    }

    BackHandler {
        viewModel.onEvent(NoteEditEvent.BackClicked)
    }

    if (isAddTagDialogVisible) {
        AddTagDialog(
            availableTags = state.availableTags,
            currentTags = state.tags,
            onDismiss = { isAddTagDialogVisible = false },
            onTagSelect = { tag -> viewModel.onEvent(NoteEditEvent.TagAdded(tag)) }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == 0L) "Новая заметка" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(NoteEditEvent.BackClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(NoteEditEvent.SaveClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Сохранить",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .imePadding()
            ) {
                TextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(NoteEditEvent.TitleChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Заголовок",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.tags.isEmpty()) {
                        AssistChip(
                            onClick = { isAddTagDialogVisible = true },
                            label = { Text("Добавить тег") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.add),
                                    contentDescription = "Добавить тег",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    } else {
                        IconButton(onClick = { isAddTagDialogVisible = true }) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "Добавить тег"
                            )
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(state.tags) { tag ->
                                TagChip(
                                    tag = tag,
                                    onRemoveClick = { viewModel.onEvent(NoteEditEvent.TagRemoved(tag)) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = state.content,
                    onValueChange = { viewModel.onEvent(NoteEditEvent.ContentChanged(it)) },
                    placeholder = { Text("Текст заметки") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedCard(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add),
                                    contentDescription = "Прикрепить фото",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    itemsIndexed(state.images) { index, uri ->
                        Box(modifier = Modifier.size(80.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Прикрепленная фотография",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedImageIndex = index }
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(16.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.onEvent(NoteEditEvent.ImageRemoved(uri)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Удалить фото",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}