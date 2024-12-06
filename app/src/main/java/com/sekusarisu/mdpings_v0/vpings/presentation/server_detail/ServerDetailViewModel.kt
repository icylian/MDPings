package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekusarisu.mdpings_v0.core.domain.util.onError
import com.sekusarisu.mdpings_v0.core.domain.util.onSuccess
import com.sekusarisu.mdpings_v0.vpings.domain.Monitor
import com.sekusarisu.mdpings_v0.vpings.domain.ServerDataSource
import com.sekusarisu.mdpings_v0.vpings.presentation.models.MonitorUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toIpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toMonitorUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toServerUi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.sequences.filter

// 使用枚举类来替代字符串常量
enum class TimeSlice(val label: String, val milliseconds: Long) {
    THIRTY_MINS("30 mins", 30 * 60 * 1000),
    ONE_HOUR("1 hour", 60 * 60 * 1000),
    THREE_HOURS("3 hours", 3 * 60 * 60 * 1000),
    SIX_HOURS("6 hours", 6 * 60 * 60 * 1000),
    ALL("all", 0);

    companion object {
        fun fromLabel(label: String): TimeSlice =
            TimeSlice.entries.find { it.label == label } ?: ALL
    }
}

// detail视图采样逻辑（原始数据的10线*1440点滑动不跟手，vico撑不住这个数据量？）
// 24h视图 -> 无论是初始载入/reload都使用sampleMonitorData()做采样 -> 每线360点
// 其他视图 -> 不做简化，直接根据epoch切片 -> 哪吒每分钟监控的情况下6h -> 每线360点

class ServerDetailViewModel(
    private val serverDataSource: ServerDataSource
): ViewModel() {

    private val _state = MutableStateFlow(ServerDetailState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ServerDetailState()
        )

    fun onAction(action: ServerDetailAction) {
        when(action) {
            is ServerDetailAction.OnLoadInfoAndMonitors -> {
                loadSelectServerInfoAndMonitors(action.serverUi, action.monitorsTimeSlice, action.apiURL, action.apiTOKEN)
            }
            is ServerDetailAction.OnMonitorsRefresh -> {
                reloadMonitors(action.serverId, action.monitorsTimeSlice, action.apiURL, action.apiTOKEN)
            }
            is ServerDetailAction.OnLoadSingleServer -> {
                loadSingleServerDetail(action.serverUi, action.apiURL, action.apiTOKEN)
            }
            is ServerDetailAction.OnSliceMonitorsTime -> {
                sliceMonitors(action.time)
            }
            is ServerDetailAction.OnDisposeCleanUp -> {
                cleanUp()
            }
        }
    }

    private fun cleanUp() {
        _state.update { it.copy(
            isLoading = false,
            isChartLoading = false,
            serverUi = null,
            ipAPIUi = null,
            monitors = emptyList(),
            monitorsOrigin = emptyList()
        ) }
    }

    private fun loadSingleServerDetail(serverUi: ServerUi, apiURL: String, apiTOKEN: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            if (apiURL.isNotEmpty() && apiTOKEN.isNotEmpty()) {
                serverDataSource
                    .getSingleServer(apiURL, apiTOKEN, serverUi.id.toString())
                    .onSuccess { server ->
                        _state.update { it.copy(
                            serverUi = server.toServerUi(),
                            isLoading = false
                        ) }
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                    }
            }
        }
    }

    private fun reloadMonitors(serverId: Int, monitorsTimeSlice: String, apiURL: String, apiTOKEN: String) {
        _state.update { it.copy(isChartLoading = true) }

        viewModelScope.launch {
            if (apiURL.isNotEmpty()) {
                serverDataSource
                    .getMonitors(apiURL, apiTOKEN, serverId)
                    .onSuccess { monitors ->
                        val monitorUi = monitors.map { it.toMonitorUi() }
                        val sampleMonitorUi = sampleMonitorData(monitorUi)
                        _state.update { it.copy(
                            monitors = sampleMonitorUi,
                            monitorsOrigin = monitorUi,
                            isChartLoading = false
                        ) }
                        sliceMonitors(monitorsTimeSlice)
                    }
                    .onError { error ->
                        _state.update { it.copy(
                            monitors = emptyList(),
                            monitorsOrigin = emptyList(),
                            isChartLoading = false,
                        ) }
                    }
            }
        }
    }

    private fun loadSelectServerInfoAndMonitors(
        serverUi: ServerUi,
        monitorsTimeSlice: String,
        apiURL: String,
        apiTOKEN: String
    ) {
        if (apiURL.isEmpty()) return

        _state.update {
            it.copy(isLoading = true, isChartLoading = true)
        }

        viewModelScope.launch {
            // 并行执行两个网络请求
            val ipApiDeferred = async {
                serverDataSource.getIpAPI(serverIp = serverUi.ipv4)
            }
            val monitorsDeferred = async {
                serverDataSource.getMonitors(apiURL, apiTOKEN, serverUi.id)
            }

            // 处理 IP API 结果
            ipApiDeferred.await()
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            ipAPIUi = result.toIpAPIUi(),
                            isLoading = false
                        )
                    }
                }
                .onError {
                    _state.update { it.copy(isLoading = false) }
                }

            // 处理 Monitors 结果
            monitorsDeferred.await()
                .onSuccess { monitors ->
                    processMonitors(monitors, monitorsTimeSlice)
                }
                .onError {
                    _state.update {
                        it.copy(
                            monitors = emptyList(),
                            monitorsOrigin = emptyList(),
                            isChartLoading = false
                        )
                    }
                }
        }
    }

    private fun processMonitors(
        monitors: List<Monitor>,
        timeSlice: String
    ) {
        val slice = TimeSlice.fromLabel(timeSlice)
        val monitorsOrigin = monitors.map { it.toMonitorUi() }

        val processedMonitors = if (slice == TimeSlice.ALL) {
            sampleMonitorData(monitorsOrigin)
        } else {
            val cutoffTime = System.currentTimeMillis() - slice.milliseconds

            monitorsOrigin.mapNotNull { monitor ->
                monitor.createdAt.asSequence()
                    .withIndex()
                    .filter { it.value > cutoffTime }
                    .toList()
                    .takeIf { it.isNotEmpty() }
                    ?.let { slicedData ->
                        val slicedIndices = slicedData.map { it.index }
                        monitor.copy(
                            createdAt = slicedData.map { it.value },
                            avgDelay = monitor.avgDelay.sliceByIndices(slicedIndices)
                        )
                    }
            }
        }

        _state.update {
            it.copy(
                monitors = processedMonitors,
                monitorsOrigin = monitorsOrigin,
                isChartLoading = false
            )
        }
    }

    private fun sampleMonitorData(
        monitors: List<MonitorUi>,
        targetSize: Int = 179  // 默认采样到360-2个点（179组，每组保留最大最小值，180组不能整除1439（哪吒API数据点）导致损失最近179分钟的数据）
    ): List<MonitorUi> {
        return monitors.map { monitor ->
            val (sampledDelays, sampledIndices) = sampleDelayWithMinMax(
                metrics = monitor.avgDelay,
                timestamps = monitor.createdAt,
                targetGroups = targetSize
            )
            monitor.copy(
                avgDelay = sampledDelays,
                createdAt = sampledIndices.map { monitor.createdAt[it] }
            )
        }
    }

    private fun sampleDelayWithMinMax(
        metrics: List<Double>,
        timestamps: List<Long>,
        targetGroups: Int
    ): Pair<List<Double>, List<Int>> {
        if (metrics.size <= targetGroups * 2) {
            return Pair(metrics, metrics.indices.toList())
        }

        val groupSize = metrics.size / targetGroups

        // 创建带索引的数据点
        val indexedMetrics = metrics.mapIndexed { index, value ->
            IndexedValue(index, value)
        }

        val sampledData = indexedMetrics.asSequence()
            .chunked(groupSize)
            .flatMap { chunk ->
                // 找出每组中的最小值和最大值及其索引
                val minEntry = chunk.minByOrNull { it.value }
                val maxEntry = chunk.maxByOrNull { it.value }

                // 按时间顺序排列最小值和最大值
                if (minEntry != null && maxEntry != null) {
                    if (minEntry.index < maxEntry.index) {
                        listOf(minEntry, maxEntry)
                    } else {
                        listOf(maxEntry, minEntry)
                    }
                } else {
                    emptyList()
                }
            }
            .take(targetGroups * 2)
            .toList()

        // 分离值和索引
        val sampledValues = sampledData.map { it.value }
        val sampledIndices = sampledData.map { it.index }

        return Pair(sampledValues, sampledIndices)
    }

    private fun sliceMonitors(timeSlice: String) {
        _state.update { currentState ->
            val slice = TimeSlice.fromLabel(timeSlice)

            if (slice == TimeSlice.ALL) {
                return@update currentState.copy(
                    monitorsTimeSlice = timeSlice,
                    monitors = sampleMonitorData(currentState.monitorsOrigin)
                )
            }

            val cutoffTime = System.currentTimeMillis() - slice.milliseconds

//            val newMonitors = currentState.monitorsOrigin.map { monitor ->
//                // 使用序列操作来优化大数据集的处理
//                val slicedData = monitor.createdAt.asSequence()
//                    .withIndex()
//                    .filter { it.value > cutoffTime }
//                    .toList()
//
//                val slicedIndices = slicedData.map { it.index }
//
//                monitor.copy(
//                    createdAt = slicedData.map { it.value },
//                    avgDelay = monitor.avgDelay.sliceByIndices(slicedIndices)
//                )
//                // 筛掉.filter { it.value > cutoffTime }后的空集
//            }.filter { monitor -> monitor.createdAt.isNotEmpty() && monitor.avgDelay.isNotEmpty() }

            val newMonitors = currentState.monitorsOrigin.mapNotNull { monitor ->
                monitor.createdAt.asSequence()
                    .withIndex()
                    .filter { it.value > cutoffTime }
                    .toList()
                    .takeIf { it.isNotEmpty() }
                    ?.let { slicedData ->
                        val slicedIndices = slicedData.map { it.index }
                        monitor.copy(
                            createdAt = slicedData.map { it.value },
                            avgDelay = monitor.avgDelay.sliceByIndices(slicedIndices)
                        )
                    }
            }

            currentState.copy(
                monitorsTimeSlice = timeSlice,
                monitors = newMonitors
            )
        }
    }

    // 扩展函数用于按索引列表切片
    private fun <T> List<T>.sliceByIndices(indices: List<Int>): List<T> =
        indices.map { this[it] }

}