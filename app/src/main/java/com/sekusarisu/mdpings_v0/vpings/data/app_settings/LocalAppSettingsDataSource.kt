package com.sekusarisu.mdpings_v0.vpings.data.app_settings

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettingsDataSource
import com.sekusarisu.mdpings_v0.vpings.domain.Instance
import com.sekusarisu.mdpings_v0.vpings.domain.ServerOrder
import com.sekusarisu.mdpings_v0.vpings.domain.ServerSortField
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeConfig
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeMode
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeSeedColor
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

private val Context.dataStore by dataStore("AppSettings.json", AppSettingsSerializer)

class LocalAppSettingsDataSource(
    private val context: Context
): AppSettingsDataSource {

    override val appSettingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            Log.e("AppSettingsDataSource", "Error reading app settings", exception)
            emit(AppSettings())
        }
        .distinctUntilChanged()

    override suspend fun getInstances(): PersistentList<Instance> {
        return try {
            Log.d("InstanceManager", "Starting to fetch instances")
            val result = context.dataStore.data.first().instances
            Log.d("InstanceManager", "Fetched instances: $result")
            result
        } catch (e: Exception) {
            Log.e("InstanceManager", "Error getting instances", e)
            persistentListOf<Instance>()
        }
    }

    override suspend fun getActiveInstance(): Instance? {
        return try {
            context.dataStore.data.first().instances[context.dataStore.data.first().activeInstance]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getActiveInstanceIndex(): Int? {
        return try {
            context.dataStore.data.first().activeInstance
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    override suspend fun setActiveInstanceIndex(index: Int) {
        context.dataStore.updateData {
            it.copy(
                activeInstance = index
            )
        }
    }

    override suspend fun getInterval(): Int {
        return try {
            context.dataStore.data.first().refreshInterval
        } catch (e: Exception) {
            e.printStackTrace()
            5000
        }
    }

    override suspend fun setInterval(interval: Int) {
        context.dataStore.updateData {
            it.copy(
                refreshInterval = interval
            )
        }
    }

    override suspend fun getServerSortField(): ServerSortField? {
        return try {
            context.dataStore.data.first().serverSortField
        } catch (e: Exception) {
            e.printStackTrace()
            ServerSortField.ID
        }
    }

    override suspend fun setServerSortField(serverSortField: ServerSortField) {
        Log.d("InstanceManager", "Starting to put instance: $serverSortField")
        context.dataStore.updateData { currentData ->
            Log.d("InstanceManager", "Current data before update: $currentData")
            val updatedData = currentData.copy(
                serverSortField = serverSortField
            )
            Log.d("InstanceManager", "Updated data: $updatedData")
            updatedData
        }
    }

    override suspend fun setServerOrder(serverOrder: ServerOrder) {
        context.dataStore.updateData {
            it.copy(
                serverOrder = serverOrder
            )
        }
    }

    override suspend fun setExpandedServerListCard(isExpanded: Boolean) {
        context.dataStore.updateData {
            it.copy(
                expandedServerListCard = isExpanded
            )
        }
    }

    override suspend fun getThemeConfig(): ThemeConfig? {
        return try {
            context.dataStore.data.first().themeConfig
        } catch (e: Exception) {
            e.printStackTrace()
            ThemeConfig()
        }
    }

    override suspend fun saveThemeConfig(
        themeMode: ThemeMode,
        themeSeedColor: ThemeSeedColor,
        isDynamicColor: Boolean
    ) {
        context.dataStore.updateData {
            it.copy(
                themeConfig = ThemeConfig(
                    themeMode = themeMode,
                    themeSeedColor = themeSeedColor,
                    isDynamicColor = isDynamicColor
                )
            )
        }
    }

    override suspend fun putInstance(name: String, apiUrl: String, apiToken: String) {
        try {
            Log.d("InstanceManager", "Starting to put instance: $name")
            context.dataStore.updateData { currentData ->
                Log.d("InstanceManager", "Current data before update: $currentData")
                val updatedData = currentData.copy(
                    instances = currentData.instances.mutate { mutableList ->
                        mutableList.add(
                            Instance(
                                name = name,
                                apiUrl = apiUrl,
                                apiToken = apiToken
                            )
                        )
                    }
                )
                Log.d("InstanceManager", "Updated data: $updatedData")
                updatedData
            }
        } catch (e: Exception) {
            Log.e("InstanceManager", "Error putting instance", e)
        }

    }

    override suspend fun editInstance(index: Int, name: String, apiUrl: String, apiToken: String) {
        try {
            Log.d("InstanceManager", "Starting to put instance: $name")
            context.dataStore.updateData { currentData ->
                Log.d("InstanceManager", "Current data before update: $currentData")
                val newInstances = currentData.instances.toMutableList()
                val oldInstance = newInstances[index]
                newInstances[index] = oldInstance.copy(
                    name = name,
                    apiUrl = apiUrl,
                    apiToken = apiToken
                )
                val updatedData = currentData.copy(instances = newInstances.toPersistentList())
                Log.d("InstanceManager", "Updated data: $updatedData")
                updatedData
            }
        } catch (e: Exception) {
            Log.e("InstanceManager", "Error putting instance", e)
        }
    }

    override suspend fun removeInstance(index: Int) {
        context.dataStore.updateData {
            it.copy(
                instances = it.instances.removeAt(index)
            )
        }
    }
}