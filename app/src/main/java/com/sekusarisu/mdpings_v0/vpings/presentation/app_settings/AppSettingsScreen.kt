package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import kotlinx.coroutines.launch

@Composable
fun AppSettingsScreen(
    state: AppSettingsState,
    onAction: (AppSettingsAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToVisualSettings: () -> Unit,
    modifier: Modifier = Modifier
) {

    var openAlertDialog by remember { mutableStateOf("") }

    when (openAlertDialog) {
        "更新间隔" -> {
            val value = state.appSettings.refreshInterval
            AlertDialogExample(
                value = value.toString(),
                onConfirmation = onAction,
                onDismissRequest = {
                    openAlertDialog = ""
                },
                dialogTitle = stringResource(R.string.app_settings_refresh_interval_dialog_title),
                dialogText = stringResource(R.string.app_settings_refresh_interval_dialog_content),
                icon = Icons.Rounded.Refresh
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .alpha(0.8f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.app_settings_settings),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        ListItem(
            modifier = Modifier.clickable(
                onClick = onNavigateToLogin
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_switch_instance),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_curd_instance),
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = "Explore",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )

        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    openAlertDialog = "更新间隔"
                }
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_refresh_interval_dialog_title),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_refresh_interval_content),
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )

        ListItem(
            modifier = Modifier.clickable(
                onClick = onNavigateToVisualSettings
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_visual_title),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_visual_content),
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.ColorLens,
                    contentDescription = "ColorLens",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )

    }
}

@Composable
fun AlertDialogExample(
    value: String = "",
    onDismissRequest: () -> Unit,
    onConfirmation: (AppSettingsAction) -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {

    val scope = rememberCoroutineScope()
    var value by rememberSaveable { mutableStateOf(value) }

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = dialogText)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = value,
                    maxLines = 1,
                    placeholder = {
                        Text(
                            text = value,
                            modifier = Modifier.alpha(0.4f)
                        )
                    },
                    shape = ShapeDefaults.ExtraLarge,
                    onValueChange = {
                        value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        onConfirmation(
                            AppSettingsAction.OnSaveIntervalClicked(value.toInt())
                        )
                        onDismissRequest()
                    }
                }
            ) {
                Text(stringResource(R.string.app_settings_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.app_settings_dialog_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppSettingsScreenPreview() {
    MDPingsTheme {
        AppSettingsScreen(
            state = AppSettingsState(AppSettings()),
            onAction = {},
            modifier = Modifier,
            onNavigateToLogin = {},
            onNavigateToVisualSettings = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertDialogExamplePreview() {
    MDPingsTheme {
        AlertDialogExample(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "API 地址",
            dialogText = "更改哪吒监控的 API 访问地址\n不建议将数据刷新间隔设置到3000ms以下",
            icon = Icons.Rounded.Explore,
        )
    }
}