package com.sekusarisu.mdpings_v0.vpings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.BuildConfig
import com.sekusarisu.mdpings_v0.R

@Preview(showBackground = true)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {

    val uriHandler = LocalUriHandler.current
    val versionName = BuildConfig.VERSION_NAME

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .alpha(0.8f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.about_about),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(R.string.about_content),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 26.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MonitorHeart,
                contentDescription = "MonitorHeart",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "MDPings",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/icylian/MDPings")
                    }
                ),
            headlineContent = { Text(text = stringResource(R.string.app_name), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(text = stringResource(R.string.about_github), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.MonitorHeart,
                    contentDescription = "MonitorHeart",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/icylian/MDPings/issues")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_github_issue), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_github_issue_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.BugReport,
                    contentDescription = "ReportProblem",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/icylian/MDPings")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_support_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = {
                Text(
                    text = stringResource(R.string.about_support_content),
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.AttachMoney,
                    contentDescription = "AttachMoney",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://t.me/mdpings_app")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_telegram_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_telegram_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/icylian/MDPings/releases")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_version_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(versionName, color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(0.9f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.about_thanks_title),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://nezha.wiki/")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_thanks_nezha_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_thanks_nezha_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.StarBorder,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://apps.apple.com/us/app/vpings/id6479573031")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_thanks_vpings_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_thanks_vpings_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://nezha-cf.buycoffee.top/")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_thanks_nezha_dash_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_thanks_nezha_dash_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.StarBorder,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
        ListItem(
            modifier = Modifier
                .clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/patrykandpatrick/vico")
                    }
                ),
            headlineContent = { Text(stringResource(R.string.about_thanks_vico_title), color = MaterialTheme.colorScheme.secondary) },
            supportingContent = { Text(stringResource(R.string.about_thanks_vico_content), color = MaterialTheme.colorScheme.tertiary) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
        )
    }
}
