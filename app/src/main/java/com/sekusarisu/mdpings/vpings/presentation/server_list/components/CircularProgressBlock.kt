package com.sekusarisu.mdpings.vpings.presentation.server_list.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings.R
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.models.WSServerUi
import com.sekusarisu.mdpings.vpings.presentation.models.toMemDiskLongDisplayableString
import kotlin.random.Random

@Composable
fun CircularProgressBlock(
    title: String,
    label: String,
    total: Float,
    used: Float
) {

    val progress = if (total > 0) (used / total).toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "AnimationProgressBar"
    )
    val progressBarColor =
        if (animatedProgress <= 0.25f) MaterialTheme.colorScheme.tertiary
        else if (animatedProgress <= 0.75f) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.error
    val animatedColor by animateColorAsState(
        animationSpec = tween(durationMillis = 1000),
        targetValue = progressBarColor,
        label = "AnimationColorProgressBar"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
            }
            CircularProgressIndicator(
                progress = { animatedProgress },
                color = animatedColor,
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
        )
    }

}

@Composable
fun CircularProgressGroup(
    server: WSServerUi,
    modifier: Modifier = Modifier
) {

    val core = "${server.host.core} Core"
    val ram = server.host.memTotal.toMemDiskLongDisplayableString()
    val disk = server.host.diskTotal.toMemDiskLongDisplayableString()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressBlock(
            title = stringResource(R.string.server_list_card_cpu),
            label = core,
            total = 100F,
            used = server.status.cpu.toFloat()
        )
        Spacer(modifier = Modifier.width(8.dp))
        CircularProgressBlock(
            title = stringResource(R.string.server_list_card_ram),
            label = ram,
            total = server.host.memTotal.toFloat(),
            used = server.status.memUsed.toFloat()
        )
        Spacer(modifier = Modifier.width(8.dp))
        CircularProgressBlock(
            label = disk,
            title = stringResource(R.string.server_list_card_disk),
            total = server.host.diskTotal.toFloat(),
            used = server.status.diskUsed.toFloat()
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun CircularProgressBlockPreview() {
    MDPingsTheme {
        CircularProgressBlock(
            title = "CPU",
            label = "2 Core",
            total = 100F,
            used = Random.nextFloat()*100
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularProgressGroupPreview() {
    MDPingsTheme {
        CircularProgressGroup(
            server = previewWSServerUi1
        )
    }
}