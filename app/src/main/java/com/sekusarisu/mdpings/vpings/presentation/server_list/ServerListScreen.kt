package com.sekusarisu.mdpings.vpings.presentation.server_list

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RunningWithErrors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.R
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.domain.AppSettings
import com.sekusarisu.mdpings.vpings.domain.ServerOrder
import com.sekusarisu.mdpings.vpings.domain.ServerSortField
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.NewServerListCard
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.ServerSummaryCard
import com.sekusarisu.mdpings.vpings.presentation.server_list.components.previewListWSServers
import kotlinx.coroutines.delay

private fun List<WSServerUi>.sortByField(serverSortField: ServerSortField, serverOrder: ServerOrder): List<WSServerUi> {
    return if (serverOrder.ordinal == 0) {
        when (serverSortField) {
            ServerSortField.ID -> sortedBy { it.id }
            ServerSortField.DISPLAY_INDEX -> sortedBy { it.displayIndex }
            ServerSortField.ONLINE -> sortedBy { it.isOnline }
            else -> sortedBy { it.id }
        }
    } else {
        when (serverSortField) {
            ServerSortField.ID -> sortedByDescending { it.id }
            ServerSortField.DISPLAY_INDEX -> sortedByDescending { it.displayIndex }
            ServerSortField.ONLINE -> sortedByDescending { it.isOnline }
            else -> sortedBy { it.id }
        }
    }
}

private fun List<WSServerUi>.filterByGroup(servers: List<Int>): List<WSServerUi> {
    return if (servers.isEmpty()) {
        filter { it -> true }
    } else {
        filter { it -> it.id in servers }
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

    LaunchedEffect(appSettingsState.appSettings.activeInstance) {
        delay(500)
        if (appSettingsState.appSettings.instances.isNotEmpty()) {
            val baseUrl = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].baseUrl
            onAction(
                ServerListAction.OnLoadWSServer(baseUrl = baseUrl)
            )
            onAction(
                ServerListAction.OnLoadServerGroup(baseUrl = baseUrl)
            )
        }
    }

    var rowFilterState by remember { mutableIntStateOf(0) }
    val titles = listOf<String>("All") + state.groups.keys.toList()
    var selectedServerGroup = state.groups.keys.toList()
        .getOrNull(rowFilterState - 1)
        ?.let { state.groups[it] }
        ?: emptyList() // 如果索引不存在，返回空列表
    var pageState = rememberPagerState {
        titles.size
    }
    val items = state.wsServers
        .sortByField(
            appSettingsState.appSettings.serverSortField,
            appSettingsState.appSettings.serverOrder
        )
        .filterByGroup(
            selectedServerGroup
        )

    // tabrow跟pager双向绑定
    LaunchedEffect(rowFilterState) {
        pageState.animateScrollToPage(rowFilterState)
    }
    LaunchedEffect(pageState.currentPage, pageState.isScrollInProgress) {
        if (!pageState.isScrollInProgress) {
            rowFilterState = pageState.currentPage
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
        visible = state.wsServers == emptyList<WSServerUi>(),
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
        visible = state.wsServers != emptyList<WSServerUi>(),
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
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // if Group Filter -> TabRow || ScrollableTabRow
            if (titles.size > 1 && titles.size <= 4) {
                TabRow(
                    selectedTabIndex = rowFilterState,
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = rowFilterState == index,
                            onClick = { rowFilterState = index },
                            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            } else if (titles.size > 4) {
                ScrollableTabRow(
                    selectedTabIndex = rowFilterState,
                    edgePadding = 0.dp
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = rowFilterState == index,
                            onClick = { rowFilterState = index },
                            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Swipeable Pager
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pageState
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Summary Card
                    item(
                        key = "ServerSummaryCard"
                    ) {
                        ServerSummaryCard(
                            isExpanded = true,
                            servers = items,
                            onAction = {},
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    // Servers Cards
                    items(
                        items = items,
                        key = { it.id }
                    ) { serverUi ->
                        NewServerListCard(
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
                wsServers = previewListWSServers,
            ),
            onLoad = {},
            appSettingsState = AppSettingsState(AppSettings())
        )
    }
}
