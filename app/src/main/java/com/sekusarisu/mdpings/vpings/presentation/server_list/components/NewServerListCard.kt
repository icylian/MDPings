package com.sekusarisu.mdpings.vpings.presentation.server_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListAction
import kotlinx.coroutines.launch

@Composable
fun NewServerListCard(
    isExpanded: Boolean,
    onNavigateToDetail: () -> Unit,
    serverUi: WSServerUi,
    onAction: (ServerListAction) -> Unit,
    modifier: Modifier = Modifier
) {

//    var isCardExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (serverUi.isOnline) {
        OutlinedCard(
            onClick = {
                scope.launch {
                    onAction(
                        ServerListAction.OnWSServerClick(
                            serverUi = serverUi
                        )
                    )
                    onNavigateToDetail()
                }
            },
            modifier = modifier.wrapContentHeight(),
            shape = ShapeDefaults.Medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            ServerTitleBlock(
                serverUi = serverUi,
                onAction = onAction,
                onNavigateToDetail = onNavigateToDetail
            )
            AnimatedVisibility(
                visible = isExpanded
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CircularProgressGroup(
                            server = serverUi,
                            modifier = Modifier.weight(2f)
                        )
                        NetworkGroup(
                            server = serverUi,
                            modifier = Modifier.weight(1f)
                        )
                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    LoadAndUptime(serverUi)
                }
            }
        }
    } else {
        Card(
            modifier = modifier.wrapContentHeight(),
            shape = ShapeDefaults.Medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            ServerTitleBlock(
                serverUi = serverUi,
                onAction = onAction,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

// Preview Data
@PreviewLightDark
@Composable
fun NewServerCardPreview() {
    MDPingsTheme {
        Column {
            NewServerListCard(
                serverUi = previewWSServerUi0,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            NewServerListCard(
                serverUi = previewWSServerUi1,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = true
            )
            Spacer(Modifier.height(8.dp))
            NewServerListCard(
                serverUi = previewWSServerUi0,
                onAction = {},
                modifier = Modifier,
                onNavigateToDetail = {},
                isExpanded = false
            )
        }
    }
}