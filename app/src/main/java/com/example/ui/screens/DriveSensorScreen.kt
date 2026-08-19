package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorDetectedPothole
import com.example.localization.AppStrings
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryContainer
import com.example.ui.theme.PolishSuccess
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishSurfaceVariant
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DriveSensorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val telemetry by viewModel.sensorTelemetry.collectAsState()
    val detections by viewModel.sensorDetections.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title & Description
        Text(
            text = AppStrings.get("auto_spotter_title", currentLanguage),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PolishTextPrimary
        )
        Text(
            text = "Smartphone Accelerometer & Gyroscope detect pothole impacts while driving.",
            fontSize = 12.sp,
            color = PolishTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drive Mode Master Control Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(PolishPrimaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = PolishPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Drive Spotter Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PolishTextPrimary
                            )
                            Text(
                                text = if (telemetry.isMonitoring) "Actively analyzing road shocks..." else "Monitoring paused",
                                fontSize = 11.sp,
                                color = if (telemetry.isMonitoring) PolishPrimary else PolishTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = telemetry.isMonitoring,
                        onCheckedChange = { isChecked ->
                            if (isChecked) viewModel.sensorManager.startMonitoring()
                            else viewModel.sensorManager.stopMonitoring()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PolishPrimary
                        ),
                        modifier = Modifier.testTag("toggle_drive_sensor_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // G-Force Shock Gauge
                Text(
                    text = "Vertical G-Force: ${String.format("%.2f", telemetry.currentGForce)}G (Peak: ${String.format("%.2f", telemetry.peakGForce)}G)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val gRatio = (telemetry.currentGForce / 3.5f).coerceIn(0.1f, 1f)
                val barColor = when {
                    telemetry.currentGForce > 2.5f -> PolishHazard
                    telemetry.currentGForce > 1.8f -> Color(0xFFEA580C)
                    telemetry.currentGForce > 1.3f -> Color(0xFFF59E0B)
                    else -> PolishSuccess
                }

                LinearProgressIndicator(
                    progress = { gRatio },
                    color = barColor,
                    trackColor = PolishSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Gyroscope Telemetry Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SensorStatBox(
                        label = "X-Tilt",
                        value = "${String.format("%.1f", telemetry.gyroTiltX)}°",
                        modifier = Modifier.weight(1f)
                    )
                    SensorStatBox(
                        label = "Y-Tilt",
                        value = "${String.format("%.1f", telemetry.gyroTiltY)}°",
                        modifier = Modifier.weight(1f)
                    )
                    SensorStatBox(
                        label = "Z-Yaw",
                        value = "${String.format("%.1f", telemetry.gyroTiltZ)}°",
                        modifier = Modifier.weight(1f)
                    )
                    SensorStatBox(
                        label = "Trip Bumps",
                        value = "${telemetry.tripPotholeCount}",
                        highlight = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Bump Simulation (Testing / Demo Tool)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🛠️ Road Impact Simulator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PolishTextPrimary
                )
                Text(
                    text = "Simulate vehicle tire hitting severe pothole to test auto-spotter logging & instant notification.",
                    fontSize = 11.sp,
                    color = PolishTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.sensorManager.simulateBump(2.2f, "Hosur Rd (Silk Board Underpass)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_moderate_bump_btn")
                    ) {
                        Text("Moderate (2.2G)", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = { viewModel.sensorManager.simulateBump(3.4f, "Outer Ring Road (Bellandur Junction)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_severe_bump_btn")
                    ) {
                        Text("Severe Shock (3.4G)", fontSize = 11.sp, color = PolishHazard)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detected Potholes Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Auto-Logged While Driving (${detections.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PolishTextPrimary
            )

            if (detections.isNotEmpty()) {
                Button(
                    onClick = { viewModel.showReviewSensorDialog.value = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("confirm_all_detections_btn")
                ) {
                    Text("Confirm & Post (${detections.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (detections.isEmpty()) {
            Surface(
                color = PolishSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PolishBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = PolishTextSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No driving impacts detected yet.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PolishTextSecondary
                    )
                    Text(
                        text = "Turn on Drive Spotter or use the simulator above to log bumps.",
                        fontSize = 11.sp,
                        color = PolishTextSecondary
                    )
                }
            }
        } else {
            detections.forEach { detection ->
                SensorDetectionItem(
                    detection = detection,
                    onConfirm = { viewModel.convertSensorDetectionToReport(detection) },
                    onDismiss = { viewModel.deleteSensorItem(detection.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SensorStatBox(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (highlight) PolishPrimaryContainer else PolishSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (highlight) PolishPrimary.copy(alpha = 0.3f) else PolishBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) PolishPrimary else PolishTextPrimary
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (highlight) PolishPrimary else PolishTextSecondary
            )
        }
    }
}

@Composable
fun SensorDetectionItem(
    detection: SensorDetectedPothole,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(detection.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(detection.timestamp))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PolishSurface),
        border = BorderStroke(1.dp, PolishBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (detection.gForceSpike > 2.5f) PolishHazard.copy(alpha = 0.15f) else PolishPrimaryContainer,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = if (detection.gForceSpike > 2.5f) PolishHazard else PolishPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "${String.format("%.1f", detection.gForceSpike)}G Shock • ${detection.addressApprox}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PolishTextPrimary
                    )
                    Text(
                        text = "Logged at $dateStr (${String.format("%.3f", detection.latitude)}, ${String.format("%.3f", detection.longitude)})",
                        fontSize = 10.sp,
                        color = PolishTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onConfirm) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = PolishSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Dismiss",
                        tint = PolishTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
