package com.sekusarisu.mdpings.vpings.presentation.server_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Commit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.MonitorUi
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailState

@Composable
fun PktLostAndAvgLatencyCard(
    state: ServerDetailState,
    monitors: List<MonitorUi>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.wrapContentHeight(),
        shape = ShapeDefaults.Medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
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
                    imageVector = Icons.Rounded.TableChart,
                    contentDescription = null,
                    modifier = Modifier
                )
                Text(
                    text = "Network Delay & Packet Loss",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .alpha(0.8f)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Commit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .weight(1f)
                    )
                    Text(
                        text = "NODE",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth()
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    text = "Delay24h",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    text = "Los24h",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    text = "Delay30",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    text = "Los30",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
            }

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
                            Text("Loading Chart...")
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
                            Text("No data available.")
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .alpha(0.8f)
                            .padding(horizontal = 4.dp)
                    ) {
                        monitors.forEachIndexed { index, monitor ->
                            PktLostAndAvgLatency(
                                monitor = monitor,
                                index = index
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PktLostAndAvgLatency(
    monitor: MonitorUi,
    index: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                tint = chartColors[index],
                imageVector = Icons.Rounded.Commit,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .weight(1f)
            )
            Text(
                text = monitor.monitorName,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            )
        }
        // avgDelay24h pktLoss24h avgDelay30mins pktLoss30mins
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = monitor.avgDelay24h,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = monitor.pktLoss24h,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = monitor.avgDelay30mins,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = monitor.pktLoss30mins,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp
        )

    }
}

@Composable
@PreviewLightDark
private fun PktLostAndAvgLatencyCardPreview() {
    MDPingsTheme {
        PktLostAndAvgLatencyCard(
            monitors = mockMonitors,
            state = ServerDetailState(),
        )
    }
}