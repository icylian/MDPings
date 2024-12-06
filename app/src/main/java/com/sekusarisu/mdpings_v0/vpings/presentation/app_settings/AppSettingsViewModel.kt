package com.sekusarisu.mdpings_v0.vpings.presentation.app_settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettingsDataSource
import com.sekusarisu.mdpings_v0.vpings.domain.Instance
import com.sekusarisu.mdpings_v0.vpings.domain.ServerOrder
import com.sekusarisu.mdpings_v0.vpings.domain.ServerSortField
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeMode
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeSeedColor
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.String

class AppSettingsViewModel(
    private val appSettingsDataSource: AppSettingsDataSource,
): ViewModel() {

    private val _state = MutableStateFlow(AppSettingsState())
    val state = _state.asStateFlow()

    init {
        // 在 ViewModel 中订阅数据变化
        viewModelScope.launch {
            appSettingsDataSource.appSettingsFlow.collect { appSettings ->
                Log.d("AppSettingsViewModel", "Received new settings: $appSettings")
                _state.update { it.copy(appSettings = appSettings) }
            }
        }
    }

    // 点击coinList coin操作
    fun onAction(action: AppSettingsAction) {
        when(action) {
            is AppSettingsAction.OnInitLoadAppSettings -> {
                viewModelScope.launch {
                    val instances = getInstances() ?: persistentListOf()
                    val activeInstance = getActiveInstanceIndex() ?: 0
                    val refreshInterval = getInterval() ?: 5000
                    _state.update {
                        it.copy(
                            appSettings = AppSettings(
                                activeInstance = activeInstance,
                                instances = instances,
                                refreshInterval = refreshInterval,
                            )
                        )
                    }
                }
            }
            is AppSettingsAction.OnChangeActiveInstance -> {
                setActiveInstanceIndex(action.index)
            }
            is AppSettingsAction.OnDeleteInstance -> {
                deleteInstance(action.index)
            }
            is AppSettingsAction.OnSaveInstanceClicked -> {
                saveInstance(action.name, action.apiUrl, action.apiToken)
            }
            is AppSettingsAction.OnSaveIntervalClicked -> {
                saveInterval(action.interval)
            }
            is AppSettingsAction.OnSaveServerSortField -> {
                saveServerSortField(action.serverSortField)
            }
            is AppSettingsAction.OnSaveServerOrder -> {
                saveServerOrder(action.serverOrder)
            }
            is AppSettingsAction.OnSaveServerListCardExpanded -> {
                saveServerListCardExpanded(action.expanded)
            }
            is AppSettingsAction.OnSaveThemeConfig -> {
                saveThemeConfig(action.themeMode, action.themeSeedColor, action.isDynamicColor)
            }
        }
    }

    fun getActiveInstanceIndex(): Int? = runBlocking {
        appSettingsDataSource.getActiveInstanceIndex()
    }

    fun setActiveInstanceIndex(index: Int) {
        viewModelScope.launch{
            appSettingsDataSource.setActiveInstanceIndex(index)
        }
    }

    fun getInstances(): PersistentList<Instance>? = runBlocking {
        appSettingsDataSource.getInstances()
    }

    fun saveInstance(name: String, apiUrl: String, apiToken: String) {
        viewModelScope.launch{
            appSettingsDataSource.putInstance(name, apiUrl, apiToken)
        }
    }

    fun deleteInstance(index: Int) {
        viewModelScope.launch{
            appSettingsDataSource.removeInstance(index)
        }
    }

    fun getApiURL(): String? = runBlocking {
        appSettingsDataSource.getActiveInstance()?.apiUrl
    }

    fun getApiTOKEN(): String? = runBlocking {
        appSettingsDataSource.getActiveInstance()?.apiToken
    }

    fun saveInterval(interval: Int) {
        viewModelScope.launch{
            appSettingsDataSource.setInterval(interval)
        }
    }

    fun getInterval(): Int? = runBlocking {
        appSettingsDataSource.getInterval()
    }

    fun saveServerSortField(serverSortField: ServerSortField) {
        viewModelScope.launch {
            appSettingsDataSource.setServerSortField(serverSortField)
        }
    }

    fun saveServerOrder(serverOrder: ServerOrder) {
        viewModelScope.launch {
            appSettingsDataSource.setServerOrder(serverOrder)
        }
    }

    fun saveServerListCardExpanded(expanded: Boolean) {
        viewModelScope.launch {
            appSettingsDataSource.setExpandedServerListCard(expanded)
        }
    }

    fun saveThemeConfig(themeMode: ThemeMode, themeSeedColor: ThemeSeedColor, isDynamicColor: Boolean) {
        viewModelScope.launch {
            appSettingsDataSource.saveThemeConfig(themeMode, themeSeedColor, isDynamicColor)
        }
    }
}