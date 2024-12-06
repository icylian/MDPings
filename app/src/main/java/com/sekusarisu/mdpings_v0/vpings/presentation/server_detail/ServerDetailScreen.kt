package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.InstanceInfo
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.NetworkMonitor
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.OfflineCard
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.PktLostAndAvgLatencyCard
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.mockIpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.mockMonitors
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.LoadAndUptime
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.NetworkIO
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.NetworkTransfer
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.OnlineStatusIndicator
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.Status
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi0
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi1
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDetailScreen(
    state: ServerDetailState,
    appSettingsState: AppSettingsState,
    selectedServerUi: ServerUi,
    onAction: (ServerDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(selectedServerUi.id) {
        val apiURL = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiUrl
        val apiTOKEN = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiToken
        val interval = appSettingsState.appSettings.refreshInterval
        onAction(
            ServerDetailAction.OnLoadInfoAndMonitors(
                serverUi = selectedServerUi,
                monitorsTimeSlice = state.monitorsTimeSlice,
                apiURL = apiURL,
                apiTOKEN = apiTOKEN,
                interval = interval
            )
        )
    }
    LaunchedEffect(selectedServerUi.id) {
        val apiURL = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiUrl
        val apiTOKEN = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiToken
        val interval = appSettingsState.appSettings.refreshInterval
        while (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            onAction(
                ServerDetailAction.OnLoadSingleServer(
                    serverUi = selectedServerUi,
                    apiURL = apiURL,
                    apiTOKEN = apiTOKEN,
                    interval = interval
                )
            )
            delay(interval.toLong())
        }
    }
    DisposableEffect(lifecycleOwner) {
        onDispose{
            onAction(
                ServerDetailAction.OnDisposeCleanUp
            )
        }
    }

    AnimatedVisibility(
        visible = (state.serverUi == null || state.ipAPIUi == null),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.server_detail_loading))
        }
    }

    AnimatedVisibility(
        visible = (state.serverUi != null && state.ipAPIUi != null),
        enter = slideInVertically(
            spring(
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (!state.serverUi!!.isOnline) {
                OfflineCard()
                Spacer(Modifier.height(8.dp))
            }
            InstanceInfo(
                serverUi = state.serverUi,
                ipAPIUi = state.ipAPIUi!!,
                modifier = modifier
            )
            Spacer(Modifier.height(8.dp))
            NetworkMonitor(
                appSettingsState = appSettingsState,
                state = state,
                onAction = onAction,
                modifier = modifier
            )
            Spacer(Modifier.height(8.dp))
            PktLostAndAvgLatencyCard(
                monitors = state.monitors,
                modifier = modifier,
                state = state
            )
            Spacer(Modifier.height(8.dp))
            ServerStatus(
                serverUi = state.serverUi,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ServerStatus(
    serverUi: ServerUi,
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
            Row(
                modifier = Modifier
                    .alpha(0.8f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.MonitorHeart,
                    contentDescription = null,
                    modifier = Modifier.weight(0.4f)
                )
                Text(
                    text = stringResource(R.string.server_detail_realtime_status),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 4.dp)
                )
                OnlineStatusIndicator(
                    isOnline = serverUi.isOnline,
                    showOnlineOrDays = "ONLINE",
                    uptime = serverUi.status.uptime,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Status(serverUi)
            Spacer(modifier = Modifier.height(2.dp))
            NetworkIO(serverUi)
            Spacer(modifier = Modifier.height(2.dp))
            NetworkTransfer(serverUi)
            Spacer(modifier = Modifier.height(2.dp))
            LoadAndUptime(serverUi)
        }
    }
}

@PreviewLightDark
@Composable
fun ServerDetailScreenPreviewOnline() {
    MDPingsTheme {
        ServerDetailScreen(
            selectedServerUi = previewServerUi0,
            modifier = Modifier,
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

@PreviewLightDark
@Composable
fun ServerDetailScreenPreviewOffline() {
    MDPingsTheme {
        ServerDetailScreen(
            selectedServerUi = previewServerUi1,
            modifier = Modifier,
            state = ServerDetailState(
                isLoading = false,
                serverUi = previewServerUi1,
                ipAPIUi = mockIpAPIUi,
                monitors = mockMonitors
            ),
            onAction = {},
            appSettingsState = AppSettingsState(AppSettings())
        )
    }
}