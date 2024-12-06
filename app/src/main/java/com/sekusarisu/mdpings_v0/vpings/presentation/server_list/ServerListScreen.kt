package com.sekusarisu.mdpings_v0.vpings.presentation.server_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RunningWithErrors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.ServerOrder
import com.sekusarisu.mdpings_v0.vpings.domain.ServerSortField
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.ServerListCard
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.ServerSummaryCard
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewListServers
import kotlinx.coroutines.delay

private fun List<ServerUi>.sortByField(serverSortField: ServerSortField, serverOrder: ServerOrder): List<ServerUi> {
    return if (serverOrder.ordinal == 0) {
        when (serverSortField) {
            ServerSortField.ID -> sortedBy { it.id }
            ServerSortField.ONLINE -> sortedBy { it.isOnline }
        }
    } else {
        when (serverSortField) {
            ServerSortField.ID -> sortedByDescending { it.id }
            ServerSortField.ONLINE -> sortedByDescending { it.isOnline }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onLoad: (AppSettingsAction) -> Unit,
    onAction: (ServerListAction) -> Unit,
    appSettingsState: AppSettingsState,
    state: ServerListState,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(appSettingsState.appSettings.activeInstance) {
        delay(500)
        if (appSettingsState.appSettings.instances.isNotEmpty()) {
            val apiURL = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiUrl
            val apiTOKEN = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].apiToken
            val interval = appSettingsState.appSettings.refreshInterval.toLong()

            while (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                onAction(
                    ServerListAction.OnLoadServer(
                        apiURL = apiURL,
                        apiTOKEN = apiTOKEN
                    )
                )
                delay(interval)
            }
        }
    }

    AnimatedVisibility(
        visible = appSettingsState.appSettings.instances.isEmpty()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.RunningWithErrors,
                contentDescription = null
            )
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.server_list_no_available_credentials))
        }
    }

    AnimatedVisibility(
        visible = state.servers == emptyList<ServerUi>() && state.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.server_list_loading_servers))
        }
    }

    AnimatedVisibility(
        visible = state.servers != emptyList<ServerUi>(),
        enter = slideInVertically(
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioMediumBouncy,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item(
                key = "Summary"
            ) {
                ServerSummaryCard(
                    isExpanded = true,
                    servers = state.servers,
                    onAction = {},
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            items(
                items = state.servers.sortByField(
                    appSettingsState.appSettings.serverSortField,
                    appSettingsState.appSettings.serverOrder
                ),
                key = { it.id }
            ) { serverUi ->
                ServerListCard(
                    isExpanded = appSettingsState.appSettings.expandedServerListCard,
                    onNavigateToDetail = { onNavigateToDetail(serverUi.id) },
                    serverUi = serverUi,
                    onAction = onAction,
                    modifier = Modifier
                        .animateItem(
                            placementSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun ServerListScreenPreview() {
    MDPingsTheme {
        ServerListScreen(
            onAction = {},
            onNavigateToDetail = {},
            state = ServerListState(
                isLoading = false,
                servers = previewListServers,
            ),
            onLoad = {},
            appSettingsState = AppSettingsState(AppSettings())
        )
    }
}
