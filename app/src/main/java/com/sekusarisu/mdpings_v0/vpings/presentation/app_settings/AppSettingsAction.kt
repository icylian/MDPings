package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings

import com.sekusarisu.mdpings_v0.vpings.domain.ServerOrder
import com.sekusarisu.mdpings_v0.vpings.domain.ServerSortField
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeMode
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeSeedColor

sealed interface AppSettingsAction {
    object OnInitLoadAppSettings: AppSettingsAction
    data class OnChangeActiveInstance(val index: Int): AppSettingsAction
    data class OnDeleteInstance(val index: Int): AppSettingsAction
    data class OnSaveInstanceClicked(val name: String, val apiUrl: String, val apiToken: String): AppSettingsAction
    data class OnSaveIntervalClicked(val interval: Int): AppSettingsAction
    data class OnSaveServerSortField(val serverSortField: ServerSortField): AppSettingsAction
    data class OnSaveServerOrder(val serverOrder: ServerOrder): AppSettingsAction
    data class OnSaveServerListCardExpanded(val expanded: Boolean): AppSettingsAction
    data class OnSaveThemeConfig(val themeMode: ThemeMode, val themeSeedColor: ThemeSeedColor, val isDynamicColor: Boolean): AppSettingsAction
}