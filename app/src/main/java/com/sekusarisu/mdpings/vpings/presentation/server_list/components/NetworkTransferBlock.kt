package com.sekusarisu.mdpings.vpings.presentation.server_list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi

@Composable
fun NetworkSpeedBlock(
    server: WSServerUi
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Speed,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .alpha(0.7f)
        )

        Spacer(Modifier.width(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.7f)
                )
                Spacer(Modifier.width(2.dp))
                AnimatedContent(
                    targetState = server.status.netOutSpeed,
                    label = "AnimatedNetworkOut",
                    transitionSpec = {
                        fadeIn() + slideInVertically(
                            animationSpec = tween(250),
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    modifier = Modifier
                ) { it ->
                    Text(
                        text = it.formatted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.7f)
                )
                Spacer(Modifier.width(2.dp))
                AnimatedContent(
                    targetState = server.status.netInSpeed,
                    label = "AnimatedNetworkIn",
                    transitionSpec = {
                        fadeIn() + slideInVertically(
                            animationSpec = tween(250),
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    modifier = Modifier
                ) { it ->
                    Text(
                        text = it.formatted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

        }
    }
}

@Composable
fun NetworkTransferBlock(
    server: WSServerUi
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.DataUsage,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .alpha(0.7f)
        )

        Spacer(Modifier.width(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Rounded.Upload,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.7f)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = server.status.netOutTransfer.formatted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.7f)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = server.status.netInTransfer.formatted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }

        }
    }
}

@Composable
fun NetworkGroup(
    server: WSServerUi,
    modifier: Modifier = Modifier
) {
    Column {
        NetworkSpeedBlock(server)
        Spacer(Modifier.height(8.dp))
        NetworkTransferBlock(server)
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkSpeedBlockPreview() {
    MDPingsTheme {
        NetworkSpeedBlock(
            server = previewWSServerUi0
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkTransferBlockPreview() {
    MDPingsTheme {
        NetworkTransferBlock(
            server = previewWSServerUi1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkGroupPreview() {
    MDPingsTheme {
        NetworkGroup(
            server = previewWSServerUi1
        )
    }
}