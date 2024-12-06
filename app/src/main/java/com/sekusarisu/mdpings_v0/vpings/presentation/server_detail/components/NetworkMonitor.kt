package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Commit
import androidx.compose.material.icons.rounded.NetworkPing
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.ServerDetailAction
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.ServerDetailState
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi0
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NetworkMonitor(
    appSettingsState: AppSettingsState,
    state: ServerDetailState,
    onAction: (ServerDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember { CartesianChartModelProducer() }
    val monitors = state.monitors
    val serverId = state.serverUi!!.id
    val isChartLoading = state.isChartLoading

    if (monitors.isNotEmpty()) {
        LaunchedEffect(monitors) {
            withContext(Dispatchers.Default) {
                modelProducer.runTransaction {
                    lineSeries {
                        monitors.map { monitor ->
                            series(
                                x = monitor.createdAt,
                                y = monitor.avgDelay
                            )
                        }
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .wrapContentHeight(),
//            .animateContentSize(),
        shape = ShapeDefaults.Medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Row(
                modifier = Modifier
                    .alpha(0.8f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.NetworkPing,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                )
                Text(
                    text = stringResource(R.string.server_detail_card_network_monitor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = {
                        val apiUrl = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiUrl
                        val apiToken = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiToken
                        onAction(
                            ServerDetailAction.OnMonitorsRefresh(serverId, state.monitorsTimeSlice, apiUrl, apiToken)
                        )
                    },
                    modifier = Modifier
                        .size(24.dp)
                ) {
                    if (isChartLoading) {
                        CircularProgressIndicator()
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // FilterChips
            SegmentedButtonRow(
                state = state,
                modifier = Modifier,
                onAction = onAction
            )

            AnimatedContent(
                targetState = monitors.isEmpty(),
                label = "AnimatedChart"
            ) { it ->
                // Loading or Chart
                if (it && state.isChartLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.server_detail_card_loading_chart))
                        }
                    }
                } else if (it) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(160.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.server_detail_card_no_monitor_data_available))
                        }
                    }
                } else {
                    MonitorsChart(
                        // TODO model = mediumLineModel Preview时解除注释，使用手动build的model测试
//                    model = mockLineModel(),
                        modelProducer = modelProducer,
                        modifier = Modifier
//                            .sizeIn(maxHeight = 240.dp)
                            .height(240.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = monitors,
                contentKey = { it -> it.size },
                label = "AnimatedTags"
            ) { it ->
                // TAG
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    it.forEachIndexed { index, monitor ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                tint = chartColors[index],
                                imageVector = Icons.Rounded.Commit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                text = monitor.monitorName,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                modifier = Modifier.alpha(0.7f)
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun DateFilterChipGroup(
    state: ServerDetailState,
    modifier: Modifier = Modifier,
    onAction: (ServerDetailAction) -> Unit = { }
) {
    Row(
        modifier = Modifier.heightIn(max = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        listOf<String>(
            stringResource(R.string.server_detail_card_30_mins),
            stringResource(R.string.server_detail_card_1_hour),
            stringResource(R.string.server_detail_card_3_hours),
            stringResource(R.string.server_detail_card_6_hours)
        ).map { text ->
            FilterChip(
                selected = state.monitorsTimeSlice == text,
                onClick = {
                    if (state.monitorsTimeSlice == text) {
                        onAction(
                            ServerDetailAction.OnSliceMonitorsTime("all")
                        )
                    } else {
                        onAction(
                            ServerDetailAction.OnSliceMonitorsTime(text)
                        )
                    }
                },
                label = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(46.dp)
                    )
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f),
                shape = ShapeDefaults.Large,
            )
        }
    }
}

@Composable
private fun SegmentedButtonRow(
    state: ServerDetailState,
    modifier: Modifier = Modifier,
    onAction: (ServerDetailAction) -> Unit = { }
) {
    val options = listOf<String>(
        stringResource(R.string.server_detail_card_30_mins),
        stringResource(R.string.server_detail_card_1_hour),
        stringResource(R.string.server_detail_card_3_hours),
        stringResource(R.string.server_detail_card_6_hours)
    )
    MultiChoiceSegmentedButtonRow(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .heightIn(max = 36.dp)
    ){
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                checked = state.monitorsTimeSlice == label,
                onCheckedChange = {
                    if (state.monitorsTimeSlice == label) {
                        onAction(
                            ServerDetailAction.OnSliceMonitorsTime("all")
                        )
                    } else {
                        onAction(
                            ServerDetailAction.OnSliceMonitorsTime(label)
                        )
                    }
                },
                label = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(46.dp)
                    )
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NetworkMonitorPreview() {
    MDPingsTheme {
        Card(
            modifier = Modifier,
            shape = ShapeDefaults.Medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            NetworkMonitor(
//                monitors = mockMonitors,
                state = ServerDetailState(
                    isLoading = false,
                    serverUi = previewServerUi0,
                    ipAPIUi = mockIpAPIUi,
                    monitors = mockMonitors
                ),
                onAction = {},
                appSettingsState = AppSettingsState(AppSettings())
            )
        }
    }
}