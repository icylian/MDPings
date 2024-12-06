package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.R
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.presentation.models.IpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.toMemDiskLongDisplayableString
import com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components.previewServerUi0

@Composable
fun InstanceInfo(
    ipAPIUi: IpAPIUi,
    serverUi: ServerUi,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.wrapContentHeight(),
        shape = ShapeDefaults.Medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Title
            Row(
                modifier = Modifier
                    .alpha(0.8f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(R.string.server_detail_card_instance_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }

            Column {
                InfoRow(
                    title = stringResource(R.string.server_detail_card_system),
                    content = "${serverUi.host.platform} ${serverUi.host.platformVersion}"
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_cpu),
                    content = serverUi.host.cpu
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_ram),
                    content = serverUi.host.memTotal.toMemDiskLongDisplayableString()
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_storage),
                    content = serverUi.host.diskTotal.toMemDiskLongDisplayableString()
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_isp),
                    content = if (ipAPIUi.isp.isNotEmpty()) ipAPIUi.isp else "N/A"
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_org),
                    content = if (ipAPIUi.org.isNotEmpty()) ipAPIUi.org else "N/A"
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_location),
                    content = "${ipAPIUi.lat}, ${ipAPIUi.lon}"
                )
                InfoRow(
                    title = stringResource(R.string.server_detail_card_nezha_version),
                    content = serverUi.host.version
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier
                .alpha(0.8f)
        )
        Spacer(
            modifier = modifier.size(4.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                .alpha(0.7f)
        )
    }
}


@Composable
@PreviewLightDark
private fun InstanceInfoPreview() {
    MDPingsTheme {
        InstanceInfo(
            serverUi = previewServerUi0,
            ipAPIUi = mockIpAPIUi ,
            modifier = Modifier.padding(16.dp)
        )
    }
}