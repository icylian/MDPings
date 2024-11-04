package com.example.mdpings.vpings.presentation.server_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.mdpings.ui.theme.MDPingsTheme
import com.example.mdpings.vpings.presentation.server_list.components.MDAppTopBar
import com.example.mdpings.vpings.presentation.server_list.components.ServerListItem
import com.example.mdpings.vpings.presentation.server_list.components.previewListServers
import com.example.mdpings.vpings.presentation.user_login.LoginScreen
import com.example.mdpings.vpings.presentation.user_login.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    state: ServerListState,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MDAppTopBar(
                scrollBehavior = scrollBehavior,
                onMenuClick = { },
                title = "MDPings",
                isLoading = state.isLoading
            )
        }
    ) { innerPadding ->
        // Loading
//        if (state.isLoading) {
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .padding(top = innerPadding.calculateTopPadding() + 4.dp)
//                    .fillMaxWidth()
//            )
//        }
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 4.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = state.servers,
                key = { it.id }
            ) { serverUi ->
                ServerListItem(
                    serverUi = serverUi,
                    onClick = { },
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
            state = ServerListState(
                isLoading = false,
                servers = previewListServers
            )
        )
    }
}
