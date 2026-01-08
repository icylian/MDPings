package com.sekusarisu.mdpings.vpings.presentation

import android.content.res.Configuration
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sekusarisu.mdpings.R
import com.sekusarisu.mdpings.Screen
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.domain.AppSettings
import com.sekusarisu.mdpings.vpings.domain.Instance
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailAction
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailScreen
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailState
import com.sekusarisu.mdpings.vpings.presentation.server_detail.components.mockIpAPIUi
import com.sekusarisu.mdpings.vpings.presentation.server_detail.components.mockMonitors
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListAction
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListScreen
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListState
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.previewListServers
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.previewServerUi0
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.previewWSServerUi0
import com.sekusarisu.mdpings.vpings.presentation.user_login.LoginAction
import kotlinx.collections.immutable.persistentListOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlin.Any

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailLayoutScreen(
    modifier: Modifier = Modifier,
    navigator: ThreePaneScaffoldNavigator<Any>,
    serverListState: ServerListState,
    serverDetailState: ServerDetailState,
    appSettingsState: AppSettingsState,
    onServerListAction: (ServerListAction) -> Unit,
    onServerDetailAction: (ServerDetailAction) -> Unit,
    onAppSettingsAction: (AppSettingsAction) -> Unit,
    onLoginAction: (LoginAction) -> Unit,
    onNavigateToTerminal: (Int) -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // 切换实例时刷新token
    LaunchedEffect(appSettingsState.appSettings.activeInstance) {
        val instances = appSettingsState.appSettings.instances
        if (instances.isNotEmpty()) {
            val instance = instances[appSettingsState.appSettings.activeInstance]
            onLoginAction(
                LoginAction.OnTestClick(instance.baseUrl, instance.username, instance.password)
            )
        }
    }
    // 切换到其他screen的时候关闭websocket
    DisposableEffect(lifecycleOwner) {
        onDispose{
            onServerListAction(
                ServerListAction.OnCloseSession
            )
        }
    }

    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = navigator,
        listPane = {
            ServerListScreen(
                state = serverListState,
                onAction = onServerListAction,
                onNavigateToDetail = { serverId ->
                    // Use the scope to launch a coroutine
                    scope.launch {
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            content = serverId
                        )
                    }
                },
                onLoad = onAppSettingsAction,
                appSettingsState = appSettingsState,
            )
        },
        detailPane = {
            AnimatedPane {
//                val selectedServerUi = navigator.currentDestination?.content ?: ""
                if (serverListState.selectedServer != null) {
                    ServerDetailScreen(
                        modifier = modifier,
                        state = serverDetailState,
                        selectedServerUi = serverListState.selectedServer,
                        onAction = onServerDetailAction,
                        appSettingsState = appSettingsState,
                        serverListState = serverListState,
                        onNavigateToTerminal = onNavigateToTerminal
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.select_a_server)
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
@Preview(showBackground = true, device = "spec:width=2560px,height=1600px,dpi=320,isRound=true",
    fontScale = 1.5f, showSystemUi = false,
    wallpaper = Wallpapers.NONE
)
@Preview(showBackground = true,
    device = "spec:width=2560px,height=1600px,dpi=320,isRound=true,orientation=portrait",
    fontScale = 1.5f, showSystemUi = false,
    wallpaper = Wallpapers.NONE
)
@Preview
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
private fun ListDetailLayoutScreenPreview() {
    MDPingsTheme {

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val windowWidthSizeClass = when {
            LocalConfiguration.current.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
            LocalConfiguration.current.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        }

        ListDetailLayoutScreen(
            modifier = Modifier,
            navigator = rememberListDetailPaneScaffoldNavigator<Any>(
                scaffoldDirective = PaneScaffoldDirective(
                    maxHorizontalPartitions = when {
                        isPortrait -> when (windowWidthSizeClass) {
                            WindowWidthSizeClass.Compact -> 1
                            WindowWidthSizeClass.Medium -> 2
                            WindowWidthSizeClass.Expanded -> 2
                            else -> 2
                        }

                        else -> 2
                    },
                    horizontalPartitionSpacerSize = 0.dp,
                    maxVerticalPartitions = 2,
                    verticalPartitionSpacerSize = 0.dp,
                    defaultPanePreferredWidth = 380.dp,
                    excludedBounds = listOf()
                )
            ),
            serverListState = ServerListState(
                isLoading = false,
                servers = previewListServers,
                selectedServer = previewWSServerUi0
            ),
            serverDetailState = ServerDetailState(
                isLoading = false,
                serverUi = previewServerUi0,
                ipAPIUi = mockIpAPIUi,
                monitors = mockMonitors
            ),
            appSettingsState = AppSettingsState(
                AppSettings().copy(
                    instances = persistentListOf(
                        Instance(
                            name = "TODO()",
                            baseUrl = "TODO()",
                            username = "TODO()",
                            password = "TODO()",
                            token = "TODO()"
                        )
                    )
                )
            ),
            onServerListAction = {},
            onServerDetailAction = {},
            onAppSettingsAction = {},
            onNavigateToTerminal = {},
            onLoginAction = {}
        )
    }
}