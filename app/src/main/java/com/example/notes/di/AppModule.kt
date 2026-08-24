package com.example.notes.di

import androidx.room.Room
import com.example.notes.data.local.db.AppDatabase
import com.example.notes.data.receiver.ReminderScheduler
import com.example.notes.data.repository.NoteRepositoryImpl
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.domain.usecase.DeleteNoteUseCase
import com.example.notes.domain.usecase.GetNoteByIdUseCase
import com.example.notes.domain.usecase.GetNotesUseCase
import com.example.notes.domain.usecase.SaveNoteUseCase
import com.example.notes.presentation.note_edit.NoteEditViewModel
import com.example.notes.presentation.notes_list.NotesListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "notes.db"
        ).build()
    }
    single { get<AppDatabase>().noteDao() }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    factory { GetNotesUseCase(get()) }
    factory { GetNoteByIdUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }

    viewModel { NotesListViewModel(get(), get()) }
    viewModel { (noteId: Long?) -> NoteEditViewModel(noteId, get(), get(), get(), get()) }
    single { ReminderScheduler(get()) }
}
