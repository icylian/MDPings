package com.sekusarisu.mdpings_v0.vpings.domain

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow

interface AppSettingsDataSource {

    val appSettingsFlow: Flow<AppSettings>

    // Instances
    suspend fun getInstances(): PersistentList<Instance>?
    suspend fun getActiveInstance(): Instance?
    suspend fun getActiveInstanceIndex(): Int?
    suspend fun setActiveInstanceIndex(index: Int)
    suspend fun putInstance(name: String, apiUrl: String, apiToken: String)
    suspend fun editInstance(index: Int, name: String, apiUrl: String, apiToken: String)
    suspend fun removeInstance(index: Int)
    // Refresh Interval
    suspend fun getInterval(): Int?
    suspend fun setInterval(interval: Int)
    // Sort && Order
    suspend fun getServerSortField(): ServerSortField?
    suspend fun setServerSortField(serverSortField: ServerSortField)
    suspend fun setServerOrder(serverOrder: ServerOrder)
    // Card Expanded
    suspend fun setExpandedServerListCard(isExpanded: Boolean)
    // Theme
    suspend fun getThemeConfig(): ThemeConfig?
    suspend fun saveThemeConfig(themeMode: ThemeMode, themeSeedColor: ThemeSeedColor, isDynamicColor: Boolean)
}
