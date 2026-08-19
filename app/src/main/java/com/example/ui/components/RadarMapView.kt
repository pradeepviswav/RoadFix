package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.localization.AppLanguage
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishSuccess
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarMapView(
    reports: List<PotholeReport>,
    currentLanguage: AppLanguage,
    onSelectReport: (PotholeReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReport by remember { mutableStateOf<PotholeReport?>(reports.firstOrNull()) }

    // Radar scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
    ) {
        // Radar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0F172A))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = minOf(size.width, size.height) * 0.42f

                // Draw Radar Grid Circles
                val ringCounts = 4
                for (i in 1..ringCounts) {
                    val r = maxRadius * (i.toFloat() / ringCounts)
                    drawCircle(
                        color = Color(0xFF334155).copy(alpha = 0.5f),
                        radius = r,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Crosshairs
                drawLine(
                    color = Color(0xFF334155).copy(alpha = 0.6f),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF334155).copy(alpha = 0.6f),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Expanding radar pulse
                drawCircle(
                    color = PolishPrimary.copy(alpha = 0.15f * (1f - pulseRadius)),
                    radius = maxRadius * pulseRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Radar beam sweep line
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    center.x + (maxRadius * cos(rad)).toFloat(),
                    center.y + (maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(PolishPrimary.copy(alpha = 0.8f), Color.Transparent),
                        start = center,
                        end = beamEnd
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.5.dp.toPx()
                )

                // User Center Point (GPS Location Pin)
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 8.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = center
                )

                // Draw Pothole Blips on Radar
                reports.forEachIndexed { index, rep ->
                    val angleOffset = (index * (360f / maxOf(1, reports.size)) + 25f)
                    val distFraction = 0.35f + ((index % 3) * 0.22f)
                    val r = maxRadius * distFraction
                    val angleRad = Math.toRadians(angleOffset.toDouble())
                    val blipPos = Offset(
                        center.x + (r * cos(angleRad)).toFloat(),
                        center.y + (r * sin(angleRad)).toFloat()
                    )

                    val blipColor = when (rep.status) {
                        ReportStatus.RESOLVED -> PolishSuccess
                        ReportStatus.WORK_IN_PROGRESS -> Color(0xFF3B82F6)
                        else -> when (rep.severity) {
                            PotholeSeverity.CRITICAL_HAZARD -> PolishHazard
                            PotholeSeverity.SEVERE -> Color(0xFFEA580C)
                            else -> Color(0xFFF59E0B)
                        }
                    }

                    val isSelected = selectedReport?.id == rep.id

                    // Halo if selected
                    if (isSelected) {
                        drawCircle(
                            color = blipColor.copy(alpha = 0.4f),
                            radius = 16.dp.toPx(),
                            center = blipPos
                        )
                    }

                    // Outer Blip
                    drawCircle(
                        color = blipColor,
                        radius = if (isSelected) 9.dp.toPx() else 6.5.dp.toPx(),
                        center = blipPos
                    )
                    // Inner Blip center
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = blipPos
                    )
                }
            }

            // Radar Status Header Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF1E293B).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(PolishPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Proximity Radar • ${reports.size} Spotted",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1E293B).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GPS 12.92°N, 77.62°E",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Legend Overlay (Bottom of Radar Canvas)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadarLegendItem(color = PolishHazard, label = "Critical")
                RadarLegendItem(color = Color(0xFFEA580C), label = "Severe")
                RadarLegendItem(color = Color(0xFF3B82F6), label = "In Progress")
                RadarLegendItem(color = PolishSuccess, label = "Fixed")
            }
        }

        // Bottom Selected Pothole Card Preview
        selectedReport?.let { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { onSelectReport(report) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PolishSurface),
                border = BorderStroke(1.dp, PolishBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = report.status)
                        SeverityChip(severity = report.severity)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PolishTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${report.address} (${report.city})",
                            fontSize = 12.sp,
                            color = PolishTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RadarLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color.White, fontSize = 10.sp)
    }
}
