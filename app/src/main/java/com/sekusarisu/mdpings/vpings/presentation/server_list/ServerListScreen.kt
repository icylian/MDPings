package com.sekusarisu.mdpings.vpings.presentation.server_list

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.ServerListCard
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.previewListServers
import kotlinx.coroutines.delay

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

    LaunchedEffect(appSettingsState.appSettings) {
        onLoad(
            AppSettingsAction.OnInitLoadAppSettings
        )
    }
    LaunchedEffect(appSettingsState.appSettings) {
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

    if (appSettingsState.appSettings.instances.isEmpty()) {
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
            Text("No Available Credentials!")
        }
    } else if (state.servers == emptyList<ServerUi>()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("Loading Servers...")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = state.servers,
                key = { it.id }
            ) { serverUi ->
                ServerListCard(
                    onNavigateToDetail = { onNavigateToDetail(serverUi.id) },
                    serverUi = serverUi,
                    onAction = onAction,
                    modifier = Modifier
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
            appSettingsState = AppSettingsState()
        )
    }
}