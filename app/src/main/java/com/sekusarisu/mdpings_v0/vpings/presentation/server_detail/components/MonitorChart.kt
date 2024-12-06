package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.ui.theme.line0
import com.sekusarisu.mdpings_v0.ui.theme.line1
import com.sekusarisu.mdpings_v0.ui.theme.line10
import com.sekusarisu.mdpings_v0.ui.theme.line11
import com.sekusarisu.mdpings_v0.ui.theme.line2
import com.sekusarisu.mdpings_v0.ui.theme.line3
import com.sekusarisu.mdpings_v0.ui.theme.line4
import com.sekusarisu.mdpings_v0.ui.theme.line5
import com.sekusarisu.mdpings_v0.ui.theme.line6
import com.sekusarisu.mdpings_v0.ui.theme.line7
import com.sekusarisu.mdpings_v0.ui.theme.line8
import com.sekusarisu.mdpings_v0.ui.theme.line9
import com.sekusarisu.mdpings_v0.ui.theme.lineDefault1
import com.sekusarisu.mdpings_v0.ui.theme.lineDefault2
import com.sekusarisu.mdpings_v0.ui.theme.lineDefault3
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Line的颜色
public val chartColors = listOf(
    lineDefault1, line0, lineDefault2, line1, lineDefault3, line2,
    line3, line4, line5, line6, line7, line8, line9, line10, line11,
    lineDefault1, line0, lineDefault2, line1, lineDefault3, line2,
    line3, line4, line5, line6, line7, line8, line9, line10, line11,
)

// x轴Epoch时间 -> LocalTime: HH:MM
private val bottomAxisValueFormatter = CartesianValueFormatter { context, x, y ->
//    context.model.extraStore.
    formatEpochTimeToHHMM(x)
}

// Epoch -> HH:MM
private fun formatEpochTimeToHHMM(epochTime: Double): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val time = Instant.ofEpochMilli(epochTime.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return time.format(formatter)
}

@Composable
//fun MonitorsChart(model: CartesianChartModel, modelProducer: CartesianChartModelProducer, modifier: Modifier) {
fun MonitorsChart(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
    CartesianChartHost(
        // model = model Preview用
//        model = model,
        modelProducer = modelProducer,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 50,
            easing = FastOutSlowInEasing
        ),
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    // 线段样式调整
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        lines =
                            chartColors.map { color ->
                                LineCartesianLayer.rememberLine(
                                    fill = remember { LineCartesianLayer.LineFill.single(fill(color)) },
                                    // 注释areaFill以降低3个monitors以上同时显示的可读性
                                    areaFill = null
                                )
                            }
                    )
                ),
                // Y轴
                startAxis =
                    VerticalAxis.rememberStart(
                        label = rememberStartAxisLabel(),
                        // 标签（暂时没用）
                        titleComponent =
                            rememberTextComponent(
                                color = Color.Black,
                                margins = dimensions(end = 4.dp),
                                padding = dimensions(8.dp, 2.dp),
                                background = rememberShapeComponent(),
                            ),
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                    ),
                // X轴
                bottomAxis =
                    HorizontalAxis.rememberBottom(
                        valueFormatter = bottomAxisValueFormatter,
                        itemPlacer =
                            remember {
                                HorizontalAxis.ItemPlacer.aligned(spacing = 4, addExtremeLabelPadding = true)
                            },
                    ),
                marker = rememberMarker(
                    labelPosition = DefaultCartesianMarker.LabelPosition.Top,
//                    showIndicator = false
                ),
                layerPadding = cartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp),
                // 名字标注
//                legend = rememberLegend(),
            ),
        modifier = modifier.fillMaxHeight(),
        zoomState = rememberVicoZoomState(
            // 初始最小放大
            zoomEnabled = true,
            initialZoom =  remember { Zoom.min(Zoom.static(), Zoom.Content) },
        ),
    )
}

// y轴坐标标签样式
@Composable
private fun rememberStartAxisLabel() =
    rememberAxisLabelComponent(
        lineCount = 1,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        margins = dimensions(4.dp),
        padding = dimensions(4.dp, 0.dp),
        background = rememberShapeComponent(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CorneredShape.rounded(4.dp),
            shadow = Shadow(
                radiusDp = 8f,
                dxDp = 0f,
                dyDp = 0f
            )
        ),
    )

// 每项的label
//@Composable
//private fun rememberLegend(): Legend<CartesianMeasuringContext, CartesianDrawingContext> {
//    val labelComponent = rememberTextComponent(vicoTheme.textColor)
//    val resources = LocalContext.current.resources
//    return rememberHorizontalLegend(
//        items =
//            rememberExtraLambda {
//                chartColors.forEachIndexed { index, color ->
//                    add(
//                        LegendItem(
//                            icon = shapeComponent(color, CorneredShape.Pill),
//                            labelComponent = labelComponent,
//                            label = monitor_name[index],
//                        )
//                    )
//                }
//            },
//        padding = dimensions(top = 8.dp, start = 16.dp, end = 16.dp)
//    )
//}

@PreviewLightDark
@Composable
fun ChartPreview() {
    val cartesianChartModelProducer = remember { CartesianChartModelProducer() }
    MDPingsTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MonitorsChart(
                // model = mediumLineModel() Preview用
//                model = mockLineModel(),
                modelProducer = cartesianChartModelProducer,
                modifier = Modifier
                    .sizeIn(maxHeight = 240.dp)
            )
        }
    }
}