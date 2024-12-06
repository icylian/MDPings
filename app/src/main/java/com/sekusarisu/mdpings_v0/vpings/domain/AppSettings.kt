package com.sekusarisu.mdpings_v0.vpings.domain

import com.sekusarisu.mdpings_v0.vpings.data.app_settings.MyPersistentListSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val activeInstance: Int = 0,
    @Serializable(with = MyPersistentListSerializer::class)
    val instances: PersistentList<Instance> = persistentListOf(),
    val refreshInterval: Int = 5000,
    val serverSortField: ServerSortField = ServerSortField.ID,
    val serverOrder: ServerOrder = ServerOrder.ASC,
    val expandedServerListCard: Boolean = true,
    val themeConfig: ThemeConfig = ThemeConfig()
)

// 哪吒监控实例
@Serializable
data class Instance(
    val name: String,
    val apiUrl: String,
    val apiToken: String
)

// 分类和排序
@Serializable
enum class ServerSortField {
    ID,
    ONLINE
}

@Serializable
enum class ServerOrder {
    ASC,
    DESC
}

// 主题
@Serializable
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Serializable
enum class ThemeSeedColor {
    RED, GREEN, BLUE, YELLOW
}

@Serializable
data class ThemeConfig(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeSeedColor: ThemeSeedColor = ThemeSeedColor.BLUE,
    val isDynamicColor: Boolean = true
)