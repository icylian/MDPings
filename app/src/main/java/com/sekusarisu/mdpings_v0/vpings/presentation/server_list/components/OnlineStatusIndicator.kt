package com.sekusarisu.mdpings_v0.vpings.presentation.server_list.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import kotlin.random.Random

@Composable
fun OnlineStatusIndicator(
    isOnline: Boolean,
    showOnlineOrDays: String = "Days",
    uptime: Long,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infinitePointSize")
    val animatedPointSizeScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pointSize"
    )
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pointSize"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp)
    ) {
        if (isOnline) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(8.dp),
                    onDraw = {
                        drawCircle(
                            color = Color.Green,
                            alpha = 0.7f
                        )
                    }
                )
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(animatedPointSizeScale)
                        .alpha(animatedAlpha),
                    onDraw = {
                        drawCircle(
                            color = Color.Green,
                            alpha = 0.2f
                        )
                    }
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                textAlign = TextAlign.Center,
                text =
                    if (showOnlineOrDays == "Days") "${uptime / 86400} Days"
                    else "ONLINE",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
            )
        } else {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(8.dp),
                    onDraw = {
                        drawCircle(
                            color = Color.Red,
                            alpha = 0.7f
                        )
                    }
                )
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(animatedPointSizeScale)
                        .alpha(animatedAlpha),
                    onDraw = {
                        drawCircle(
                            color = Color.Red,
                            alpha = 0.2f
                        )
                    }
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                textAlign = TextAlign.Center,
                text = "OFFLINE",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OnlineStatusIndicatorPreview() {
    MDPingsTheme {
        Column {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                OnlineStatusIndicator(
                    isOnline = true,
                    uptime= Random.nextLong(until = 99999999)
                )
            }
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                OnlineStatusIndicator(
                    isOnline = false,
                    uptime= Random.nextLong(until = 99999999)
                )
            }
        }
    }
}