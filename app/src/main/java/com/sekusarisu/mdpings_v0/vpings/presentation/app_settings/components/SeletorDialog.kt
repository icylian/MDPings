package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsAction

@Composable
fun SelectorDialog(
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: (AppSettingsAction) -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            content()
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {},
        dismissButton = {}
    )
}