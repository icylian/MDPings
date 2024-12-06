package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.child_screens

import android.content.Intent
import android.os.Build
import com.sekusarisu.mdpings_v0.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.HdrAuto
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeConfig
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeMode
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeSeedColor
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.components.AppSettingsSwitchWithDivider
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.components.SelectorDialog
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.ServerListCard
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi0
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi1
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi2
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.components.AppSettingsSwitch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun VisualSettingsScreen(
    state: AppSettingsState,
    onAction: (AppSettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    var openAlertDialog by remember { mutableStateOf("") }
    val context = LocalContext.current
    val alternativeLanguage = context.resources.configuration.locales.get(0).toLanguageTag()

    when (openAlertDialog) {
        "动态颜色" -> {
            SelectorDialog(
                onConfirmation = onAction,
                onDismissRequest = {
                    openAlertDialog = ""
                },
                dialogTitle = stringResource(R.string.app_settings_visual_dynamic_color_dialog_title),
                dialogText = stringResource(R.string.app_settings_visual_dynamic_color_dialog_content),
                icon = Icons.Rounded.ColorLens,
                content = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_settings_visual_dynamic_color_dialog_content),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        listOf<String>(
                            stringResource(R.string.app_settings_visual_theme_color_red),
                            stringResource(R.string.app_settings_visual_theme_color_green),
                            stringResource(R.string.app_settings_visual_theme_color_blue),
                            stringResource(R.string.app_settings_visual_theme_color_yellow)
                        ).forEachIndexed { index, colorString ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        onAction(
                                            AppSettingsAction.OnSaveThemeConfig(
                                                themeMode = state.appSettings.themeConfig.themeMode,
                                                themeSeedColor = ThemeSeedColor.entries[index],
                                                isDynamicColor = false
                                            )
                                        )
                                    }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.appSettings.themeConfig.themeSeedColor == ThemeSeedColor.entries[index],
                                    onClick = {
                                        onAction(
                                            AppSettingsAction.OnSaveThemeConfig(
                                                themeMode = state.appSettings.themeConfig.themeMode,
                                                themeSeedColor = ThemeSeedColor.entries[index],
                                                isDynamicColor = false
                                            )
                                        )
                                    }
                                )
                                Text(
                                    text = colorString,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
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
        // Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ColorLens,
                contentDescription = "ColorLens",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.app_settings_visual_title),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        // Preview ServerListCard
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            ServerListCard(
                serverUi = previewServerUi0,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            ServerListCard(
                serverUi = previewServerUi1,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            ServerListCard(
                serverUi = previewServerUi2,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = false
            )
        }

        Spacer(Modifier.height(16.dp))

        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    openAlertDialog = "动态颜色"
                }
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_visual_dynamic_color_title),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_visual_dynamic_color_content),
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Colorize,
                    contentDescription = "Colorize",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            trailingContent = {
                AppSettingsSwitchWithDivider(
                    checked = state.appSettings.themeConfig.isDynamicColor,
                    onCheckedChange = {
                        onAction(
                            AppSettingsAction.OnSaveThemeConfig(
                                themeMode = state.appSettings.themeConfig.themeMode,
                                themeSeedColor = state.appSettings.themeConfig.themeSeedColor,
                                isDynamicColor = !state.appSettings.themeConfig.isDynamicColor
                            )
                        )
                    }
                )
            }
        )
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    onAction(
                        AppSettingsAction.OnSaveThemeConfig(
                            themeMode = when(state.appSettings.themeConfig.themeMode) {
                                ThemeMode.SYSTEM -> if (isSystemInDarkTheme) ThemeMode.DARK else ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                ThemeMode.DARK -> ThemeMode.SYSTEM
                            },
                            themeSeedColor = state.appSettings.themeConfig.themeSeedColor,
                            isDynamicColor = state.appSettings.themeConfig.isDynamicColor
                        )
                    )
                }
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_visual_theme_follow_title),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_visual_theme_follow_content),
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.HdrAuto,
                    contentDescription = "HdrAuto",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            trailingContent = {
                AppSettingsSwitch(
                    checked = state.appSettings.themeConfig.themeMode == ThemeMode.SYSTEM,
                    onCheckedChange = {
                        onAction(
                            AppSettingsAction.OnSaveThemeConfig(
                                themeMode = when(state.appSettings.themeConfig.themeMode) {
                                    ThemeMode.SYSTEM -> if (isSystemInDarkTheme) ThemeMode.DARK else ThemeMode.LIGHT
                                    ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                    ThemeMode.DARK -> ThemeMode.SYSTEM
                                },
                                themeSeedColor = state.appSettings.themeConfig.themeSeedColor,
                                isDynamicColor = state.appSettings.themeConfig.isDynamicColor
                            )
                        )
                    }
                )
            }
        )
        AnimatedVisibility(
            visible = state.appSettings.themeConfig.themeMode != ThemeMode.SYSTEM
        ) {
            ListItem(
                modifier = Modifier.clickable(
                    onClick = {
                        onAction(
                            AppSettingsAction.OnSaveThemeConfig(
                                themeMode = if (state.appSettings.themeConfig.themeMode == ThemeMode.LIGHT) ThemeMode.DARK else ThemeMode.LIGHT,
                                themeSeedColor = state.appSettings.themeConfig.themeSeedColor,
                                isDynamicColor = state.appSettings.themeConfig.isDynamicColor
                            )
                        )
                    }
                ),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.app_settings_visual_dark_mode_title),
                        color = MaterialTheme.colorScheme.secondary
                    ) },
                supportingContent = {
                    Text(text = stringResource(R.string.app_settings_visual_dark_mode_content),
                        color = MaterialTheme.colorScheme.tertiary
                    ) },
                leadingContent = {
                    AnimatedContent(
                        targetState = state.appSettings.themeConfig.themeMode,
                        label = "AnimatedLeadingContent"
                    ) {
                        when(it) {
                            ThemeMode.SYSTEM -> {
                                if (isSystemInDarkTheme) {
                                    Icon(
                                        imageVector = Icons.Rounded.DarkMode,
                                        contentDescription = "DarkMode",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.LightMode,
                                        contentDescription = "LightMode",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            ThemeMode.LIGHT -> {
                                Icon(
                                    imageVector = Icons.Rounded.LightMode,
                                    contentDescription = "LightMode",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            ThemeMode.DARK -> {
                                Icon(
                                    imageVector = Icons.Rounded.DarkMode,
                                    contentDescription = "DarkMode",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                },
                trailingContent = {
                    AppSettingsSwitch(
                        checked =
                        state.appSettings.themeConfig.themeMode == ThemeMode.SYSTEM && isSystemInDarkTheme()
                                || state.appSettings.themeConfig.themeMode == ThemeMode.DARK,
                        onCheckedChange = {
                            onAction(
                                AppSettingsAction.OnSaveThemeConfig(
                                    themeMode = if (state.appSettings.themeConfig.themeMode != ThemeMode.DARK) ThemeMode.DARK else ThemeMode.LIGHT,
                                    themeSeedColor = state.appSettings.themeConfig.themeSeedColor,
                                    isDynamicColor = state.appSettings.themeConfig.isDynamicColor
                                )
                            )
                        }
                    )
                }
            )
        }
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "当前系统（小于Android13）不支持应用语言单独设置。",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ),
            headlineContent = {
                Text(
                    text = stringResource(R.string.app_settings_visual_language_title),
                    color = MaterialTheme.colorScheme.secondary
                ) },
            supportingContent = {
                Text(text = alternativeLanguage,
                    color = MaterialTheme.colorScheme.tertiary
                ) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = "Language",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        )

    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
@Preview(showBackground = true)
fun VisualSettingsScreenPreview() {
    MDPingsTheme {
        VisualSettingsScreen(
            state = AppSettingsState().copy(
                appSettings = AppSettings().copy(
                    themeConfig = ThemeConfig().copy(
                        themeMode = ThemeMode.LIGHT
                    )
                )
            ),
            onAction = {}
        )
    }
}