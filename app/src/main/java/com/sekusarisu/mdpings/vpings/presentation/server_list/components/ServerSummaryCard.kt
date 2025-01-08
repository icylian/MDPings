package com.sekusarisu.mdpings.vpings.presentation.server_list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.AlignHorizontalLeft
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.R
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi
import com.sekusarisu.mdpings.vpings.presentation.models.toNetIOSpeedDisplayableString
import com.sekusarisu.mdpings.vpings.presentation.models.toNetTRLongDisplayableString
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListAction

@Composable
fun ServerSummaryCard(
    isExpanded: Boolean,
    servers: List<WSServerUi>,
    onAction: (ServerListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(max = 240.dp),
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
                    imageVector = Icons. AutoMirrored.Rounded.AlignHorizontalLeft,
                    contentDescription = null,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.server_list_summary_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .alpha(0.8f)
                    .fillMaxWidth()
                    .weight(2f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryOutlinedCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.server_list_summary_count),
                    content = {
                        OutlinedCardOnlineCount(
                            counts = servers.size,
                            color = Color.Blue
                        )
                    }
                )
                Spacer(Modifier.width(16.dp))
                SummaryOutlinedCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.server_list_summary_status),
                    content = {
                        OutlinedCardOnlineCount(
                            modifier = Modifier
                                .weight(1f),
                            counts = servers.filter { server -> !server.isOnline }.size,
                            color = Color.Red
                        )
                        OutlinedCardOnlineCount(
                            modifier = Modifier
                                .weight(1f),
                            counts = servers.filter { server -> server.isOnline }.size,
                            color = Color.Green
                        )
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .alpha(0.8f)
//                    .heightIn(max = 84.dp)
                    .fillMaxWidth()
                    .weight(3f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                SummaryOutlinedCard(
//                    modifier = Modifier
//                        .weight(1f),
//                    title = "离线服务器",
//                    content = {
//                        OutlinedCardOnlineCount(
//                            counts = servers.filter { server -> !server.isOnline }.size,
//                            color = Color.Red
//                        )
//                    }
//                )
//                Spacer(Modifier.width(16.dp))
                SummaryOutlinedCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.server_list_summary_transfer),
                    content = {
                        OutlinedCardTransferredCount(
                            netInTransfer = servers.sumOf { it.status.netInTransfer.value },
                            netOutTransfer = servers.sumOf { it.status.netOutTransfer.value },
                            netInSpeed = servers.sumOf { it.status.netInSpeed.value },
                            netOutSpeed = servers.sumOf { it.status.netOutSpeed.value }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryOutlinedCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier
//            .fillMaxHeight(),
//        shape = ShapeDefaults.Medium,
//        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }

    }
}

@Composable
fun OutlinedCardOnlineCount(
    modifier: Modifier = Modifier,
    color: Color,
    counts: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinitePointSize")
    val animatedPointSizeScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pointSize"
    )
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pointSize"
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(8.dp),
            onDraw = {
                drawCircle(
                    color = color,
                    alpha = 0.7f
                )
            }
        )
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .scale(animatedPointSizeScale)
                .alpha(animatedAlpha),
            onDraw = {
                drawCircle(
                    color = color,
                    alpha = 0.2f
                )
            }
        )
    }
    Text(
        text = "$counts",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(horizontal = 4.dp)
    )
}

@Composable
fun OutlinedCardTransferredCount(
    modifier: Modifier = Modifier,
    netInSpeed: Long,
    netOutSpeed: Long,
    netInTransfer: Long,
    netOutTransfer: Long
) {
    Row(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = null
                )
                AnimatedContent(
                    targetState = netOutSpeed.toNetIOSpeedDisplayableString().formatted,
                    label = "AnimatedNetworkIn",
                    transitionSpec = {
                        fadeIn() + slideInVertically(
                            animationSpec = tween(250),
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                ) { it ->
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = null
                )
                AnimatedContent(
                    targetState = netInSpeed.toNetIOSpeedDisplayableString().formatted,
                    label = "AnimatedNetworkIn",
                    transitionSpec = {
                        fadeIn() + slideInVertically(
                            animationSpec = tween(250),
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                ) { it ->
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Rounded.Upload,
                    contentDescription = null
                )
                Text(
                    text = netOutTransfer.toNetTRLongDisplayableString().formatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = null
                )
                Text(
                    text = netInTransfer.toNetTRLongDisplayableString().formatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ServerSummaryCardPreview() {
    MDPingsTheme {
        ServerSummaryCard(
            isExpanded = true,
            servers = previewListWSServers,
            onAction = {},
            modifier = Modifier
        )
    }
}