package com.sekusarisu.mdpings_v0.vpings.presentation

import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.Instance
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.ServerDetailAction
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.ServerDetailScreen
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.ServerDetailState
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.mockIpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components.mockMonitors
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.ServerListAction
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.ServerListScreen
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.ServerListState
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewListServers
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi0
import kotlinx.collections.immutable.persistentListOf
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
    onAppSettingsAction: (AppSettingsAction) -> Unit
) {
    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = navigator,
        listPane = {
            ServerListScreen(
                modifier = modifier,
                state = serverListState,
                onAction = onServerListAction,
                onNavigateToDetail = { serverId ->
                    navigator.navigateTo(
                        pane = ListDetailPaneScaffoldRole.Detail,
                        content = serverId
                    )
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
    wallpaper = androidx.compose.ui.tooling.preview.Wallpapers.NONE
)
@Preview(showBackground = true,
    device = "spec:width=2560px,height=1600px,dpi=320,isRound=true,orientation=portrait",
    fontScale = 1.5f, showSystemUi = false,
    wallpaper = androidx.compose.ui.tooling.preview.Wallpapers.NONE
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
                selectedServer = previewServerUi0
            ),
            serverDetailState = ServerDetailState(
                isLoading = false,
                serverUi = previewServerUi0,
                ipAPIUi = mockIpAPIUi,
                monitors = mockMonitors
            ),
            appSettingsState = AppSettingsState(AppSettings().copy(
                instances = persistentListOf(Instance(
                    name = "TODO()",
                    apiUrl = "TODO()",
                    apiToken = "TODO()"
                ))
            )),
            onServerListAction = {},
            onServerDetailAction = {},
            onAppSettingsAction = {}
        )
    }
}