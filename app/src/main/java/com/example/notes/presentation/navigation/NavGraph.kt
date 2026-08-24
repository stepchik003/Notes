package com.example.notes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.notes.presentation.note_edit.NoteEditScreen
import com.example.notes.presentation.notes_list.NotesListScreen

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route
    ) {
        composable(route = Screen.NotesList.route) {
            NotesListScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEdit.passNoteId(noteId))
                },
                onAddNoteClick = {
                    navController.navigate(Screen.NoteEdit.passNoteId())
                }
            )
        }

        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "app://notes/edit/{noteId}" }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")
                .takeIf { it != -1L }

            NoteEditScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}