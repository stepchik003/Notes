package com.example.notes.presentation.components
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        showNativePicker(
            context = context,
            onDismiss = onDismiss,
            onTimeSelected = onTimeSelected
        )
    }
}

private fun showNativePicker(
    context: Context,
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    onTimeSelected(calendar.timeInMillis)
                    onDismiss()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.setOnCancelListener { onDismiss() }
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.setOnCancelListener { onDismiss() }
    datePickerDialog.show()
}