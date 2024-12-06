package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings

import androidx.compose.runtime.Immutable
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings

@Immutable
data class AppSettingsState(
    val appSettings: AppSettings = AppSettings()
)