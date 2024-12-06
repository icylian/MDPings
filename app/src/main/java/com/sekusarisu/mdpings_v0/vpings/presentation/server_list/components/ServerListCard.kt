package com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.presentation.models.HostUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.StatusUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.countryCodeCheck
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toCountryCodeToEmojiFlag
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toDisplayableNumber
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toNetTRLongDisplayableString
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toNetIOSpeedDisplayableString
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.ServerListAction
import kotlinx.coroutines.launch
import kotlin.random.Random

// 锁定文字不根据系统缩放走
//val TextUnit.nonScaledSp
//    @Composable
//    get() = (this.value / LocalDensity.current.fontScale).sp

private fun String.toLetterSpacing() = when(this) {
    "CPU" -> 5.5.sp
    "RAM" -> 4.5.sp
    "SWAP" -> 0.7.sp
    "DISK" -> 3.sp
    "NetIO" -> 1.2.sp
    "NetTR" -> 0.sp
    "LOAD" -> 1.7.sp
    else -> 0.sp
}

@Composable
fun ServerListCard(
    isExpanded: Boolean,
    onNavigateToDetail: () -> Unit,
    serverUi: ServerUi,
    onAction: (ServerListAction) -> Unit,
    modifier: Modifier = Modifier
) {

//    var isCardExpanded by remember { mutableStateOf(false) }

    if (serverUi.isOnline) {
        Card(
//            onClick = {
//                isCardExpanded = !isCardExpanded
//            },
            modifier = modifier.wrapContentHeight(),
            shape = ShapeDefaults.Medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            NewServerTitle(
                serverUi = serverUi,
                onAction = onAction,
                onNavigateToDetail = onNavigateToDetail
            )
            AnimatedVisibility(
                visible = isExpanded
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Status(serverUi)
                    Spacer(modifier = Modifier.height(2.dp))
                    NetworkIO(serverUi)
                    Spacer(modifier = Modifier.height(2.dp))
                    NetworkTransfer(serverUi)
                    Spacer(modifier = Modifier.height(2.dp))
                    LoadAndUptime(serverUi)
                }
            }
//            AnimatedVisibility(
//                visible = !isExpanded
//            ) {
//                Column(
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = modifier
//                        .padding(horizontal = 12.dp)
//                        .padding(bottom = 8.dp)
//                ) {
//                    NetworkIOCollapse(server = serverUi)
//                }
//            }
        }
    } else {
        Card(
            modifier = modifier.wrapContentHeight(),
            shape = ShapeDefaults.Medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            NewServerTitle(
                serverUi = serverUi,
                onAction = onAction,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

@Composable
fun ServerTitle(
    serverUi: ServerUi,
    onAction: (ServerListAction) -> Unit,
    onNavigateToDetail: () -> Unit
) {

    val scope = rememberCoroutineScope()

    FilterChip(
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor =
                if (serverUi.isOnline) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer,
            selectedLabelColor =
                if (serverUi.isOnline) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.error,
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp),
        leadingIcon = {
            if (serverUi.isOnline) Text(text = serverUi.host.countryCode)
            else Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        trailingIcon = {
            Text(
                text =
                    if (serverUi.isOnline) "${serverUi.host.platform}${serverUi.host.platformVersion}"
                    else "OFFLINE",
                color =
                    if (serverUi.isOnline) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .alpha(0.7f)
            )
        },
        shape = ShapeDefaults.Large,
        selected = true,
        onClick = {
            scope.launch {
                onAction(
                    ServerListAction.OnServerClick(
                        serverUi = serverUi
                    )
                )
                onNavigateToDetail()
            }
        },
        label = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = serverUi.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
}

@Composable
fun Status(server: ServerUi) {
    ProgressBar(
        text = stringResource(R.string.server_list_card_cpu),
        total = 100F,
        used = server.status.cpu.toFloat()
    )
    Spacer(modifier = Modifier.height(2.dp))
    ProgressBar(
        text = stringResource(R.string.server_list_card_ram),
        total = server.host.memTotal.toFloat(),
        used = server.status.memUsed.toFloat()
    )
    Spacer(modifier = Modifier.height(2.dp))
    ProgressBar(
        text = stringResource(R.string.server_list_card_swap),
        total = server.host.swapTotal.toFloat(),
        used = server.status.swapUsed.toFloat()
    )
    Spacer(modifier = Modifier.height(2.dp))
    ProgressBar(
        text = stringResource(R.string.server_list_card_disk),
        total = server.host.diskTotal.toFloat(),
        used = server.status.diskUsed.toFloat()
    )
}

@Composable
fun ProgressBar(text: String, total: Float, used: Float) {

    val progress = if (total > 0) (used / total).toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "AnimationProgressBar"
    )
    val progressBarColor =
        if (animatedProgress <= 0.25f) MaterialTheme.colorScheme.tertiary
        else if (animatedProgress <= 0.75f) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.error
    val animatedColor by animateColorAsState(
        animationSpec = tween(durationMillis = 1000),
        targetValue = progressBarColor,
        label = "AnimationColorProgressBar"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = text.toLetterSpacing()
                ),
                modifier = Modifier
                    .width(48.dp)
                    .alpha(0.8f)
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            color = animatedColor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
                .height(4.5.dp)
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier
                .width(48.dp),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadAndUptime(server: ServerUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
        ) {
            Text(
                text = stringResource(R.string.server_list_card_load),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = "LOAD".toLetterSpacing()
                ),
                modifier = Modifier
                    .width(48.dp)
                    .alpha(0.8f)
            )
        }
        AnimatedContent(
            targetState = server.status,
            label = "AnimatedNetworkIO",
            transitionSpec = {
                fadeIn() + slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) togetherWith fadeOut(animationSpec = tween(200))
            },
            modifier = Modifier
                .weight(3.2f)
        ) { it ->
            Text(
                textAlign = TextAlign.Center,
                text = "${it.load1.formatted} | ${it.load5.formatted} | ${it.load15.formatted}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = stringResource(R.string.server_list_card_uptime),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .alpha(0.8f)
        )
        Text(
            textAlign = TextAlign.Center,
            text = "${server.status.uptime / 86400} D",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun NetworkTransfer(server: ServerUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.server_list_card_nettr),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .width(48.dp)
                    .alpha(0.8f)
            )
        }
        Icon(
            imageVector = Icons.Rounded.Download,
            contentDescription = null,
            modifier = Modifier
                .weight(0.5f)
                .alpha(0.7f)
        )
        Text(
            text = server.status.netInTransfer.formatted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
        )
        Icon(
            imageVector = Icons.Rounded.Upload,
            contentDescription = null,
            modifier = Modifier
                .weight(0.5f)
                .alpha(0.7f)
        )
        Text(
            text = server.status.netOutTransfer.formatted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NetworkIO(server: ServerUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.server_list_card_netio),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = "NetIO".toLetterSpacing()
                ),
                modifier = Modifier
                    .width(48.dp)
                    .alpha(0.8f)
            )
        }
        Icon(
            imageVector = Icons.Rounded.ArrowDownward,
            contentDescription = null,
            modifier = Modifier
                .weight(0.5f)
                .alpha(0.7f)
        )
        AnimatedContent(
            targetState = server.status.netInSpeed,
            label = "AnimatedNetworkIn",
            transitionSpec = {
                fadeIn() + slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) togetherWith fadeOut(animationSpec = tween(200))
            },
            modifier = Modifier
                .weight(1f)
        ) { it ->
            Text(
                text = it.formatted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ArrowUpward,
            contentDescription = null,
            modifier = Modifier
                .weight(0.5f)
                .alpha(0.7f)
        )
        AnimatedContent(
            targetState = server.status.netOutSpeed,
            label = "AnimatedNetworkOut",
            transitionSpec = {
                fadeIn() + slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) togetherWith fadeOut(animationSpec = tween(200))
            },
            modifier = Modifier
                .weight(1f)
        ) { it ->
            Text(
                text = it.formatted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun NetworkIOCollapse(
    modifier: Modifier = Modifier,
    server: ServerUi
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

    }
}

// Preview Data
@PreviewLightDark
@Composable
fun ServerCardPreview() {
    MDPingsTheme {
        Column {
            ServerListCard(
                serverUi = previewServerUi0,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            ServerListCard(
                serverUi = previewServerUi1,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            ServerListCard(
                serverUi = previewServerUi0,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = false
            )
        }
    }
}

private fun previewHostUi(): HostUi {
    return HostUi(
        platform = "ubuntu",
        platformVersion = "22.04",
        cpu = "Cortex-A55 8 Physical Core\nCortex-A76 8 Physical Core",
        memTotal = 8323002368,
        diskTotal = 2164154892288,
        swapTotal = 267362304,
        arch = "x86_64",
        virtualization = "kvm",
        bootTime = 1725353936,
        countryCode = listOf<String>(
            "hk", "nl", "us", "cn", "jp", "tw"
        )[Random.nextInt(until = 6)]
            .countryCodeCheck().toCountryCodeToEmojiFlag(),
        version = "0.20.3"
    )
}

private fun previewStatusUi(): StatusUi {
    return StatusUi(
        cpu = Random.nextDouble(from = 0.0, until = 10.0),
        memUsed = Random.nextLong(until = 8323002368),
        swapUsed = Random.nextLong(until = 267362304),
        diskUsed = Random.nextLong(until = 2164154892288),
        netInTransfer = Random.nextLong(from = 0, until = 1024000000000000).toNetTRLongDisplayableString(),
        netOutTransfer = Random.nextLong(from = 0, until = 1024000000000000000).toNetTRLongDisplayableString(),
        netInSpeed = Random.nextLong(until = 1024000000).toNetIOSpeedDisplayableString(),
        netOutSpeed = Random.nextLong(until = 1024000000).toNetIOSpeedDisplayableString(),
        uptime = Random.nextLong(until = 102400000),
        load1 = Random.nextDouble(until = 200.0).toDisplayableNumber(),
        load5 = Random.nextDouble(until = 200.0).toDisplayableNumber(),
        load15 = Random.nextDouble(until = 200.0).toDisplayableNumber(),
        tcpConnCount = 63,
        udpConnCount = 97,
        processCount = 296,
        gpu = 0
    )
}

internal val previewServerUi0 = ServerUi(
    id = 0,
    name = "Server0",
    tag = "",
    lastActive = 1730554945,
    ipv4 = "1.1.1.1",
    ipv6 = "2001:0000:130F:0000:0000:09C0:876A:130B",
    validIp = "1.1.1.1",
    displayIndex = 0,
    hideForGuest = false,
    host = previewHostUi(),
    status = previewStatusUi(),
    isOnline = true
)

internal val previewServerUi1 = ServerUi(
    id = 1,
    name = "Server1",
    tag = "",
    lastActive = 1730554945,
    ipv4 = "1.1.1.1",
    ipv6 = "2001:0000:130F:0000:0000:09C0:876A:130B",
    validIp = "1.1.1.1",
    displayIndex = 0,
    hideForGuest = false,
    host = previewHostUi(),
    status = previewStatusUi(),
    isOnline = false
)

internal val previewServerUi2 = ServerUi(
    id = 2,
    name = "Server2",
    tag = "",
    lastActive = 1730554945,
    ipv4 = "1.1.1.1",
    ipv6 = "2001:0000:130F:0000:0000:09C0:876A:130B",
    validIp = "1.1.1.1",
    displayIndex = 0,
    hideForGuest = false,
    host = previewHostUi(),
    status = previewStatusUi(),
    isOnline = true
)

internal val previewServerUi3 = ServerUi(
    id = 3,
    name = "Server3",
    tag = "",
    lastActive = 1730554945,
    ipv4 = "1.1.1.1",
    ipv6 = "2001:0000:130F:0000:0000:09C0:876A:130B",
    validIp = "1.1.1.1",
    displayIndex = 0,
    hideForGuest = false,
    host = previewHostUi(),
    status = previewStatusUi(),
    isOnline = false
)

internal val previewListServers = listOf<ServerUi>(
    previewServerUi0, previewServerUi1, previewServerUi2, previewServerUi3
)