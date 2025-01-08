package com.sekusarisu.mdpings.vpings.presentation.server_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListAction
import kotlinx.coroutines.launch

@Composable
fun ServerTitleBlock(
    serverUi: WSServerUi,
    onAction: (ServerListAction) -> Unit,
    onNavigateToDetail: () -> Unit
) {

    val scope = rememberCoroutineScope()

    FilterChip(
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor =
            if (serverUi.isOnline) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
            labelColor =
            if (serverUi.isOnline) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.error,
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp),
        leadingIcon = {
            if (serverUi.isOnline)
                Text(
                    text = serverUi.countryCode
                )
            else Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        trailingIcon = {
            OnlineStatusIndicator(
                isOnline = serverUi.isOnline,
                uptime = serverUi.status.uptime,
                showLightSpot = false
            )
        },
        shape = ShapeDefaults.Large,
        selected = false,
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
        },
        border = null
    )
}

@PreviewLightDark
@Composable
fun ServerTitleBlockPreview() {
    MDPingsTheme {
        ServerTitleBlock(
            serverUi = previewWSServerUi0,
            onAction = {},
            onNavigateToDetail = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ServerTitleBlockPreview1() {
    MDPingsTheme {
        ServerTitleBlock(
            serverUi = previewWSServerUi1,
            onAction = {},
            onNavigateToDetail = {}
        )
    }
}