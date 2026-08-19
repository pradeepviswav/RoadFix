package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.localization.AppStrings
import com.example.ui.components.PotholeCard
import com.example.ui.components.RadarMapView
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishBorderHighlight
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryContainer
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishSurfaceVariant
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ViewMode

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val filteredReports by viewModel.filteredReports.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCity by viewModel.selectedCityFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()
    val sensorTelemetry by viewModel.sensorTelemetry.collectAsState()
    val sensorDetections by viewModel.sensorDetections.collectAsState(initial = emptyList())

    val cities = listOf("All India", "Bengaluru", "Mumbai", "Delhi-NCR", "Chennai", "Hyderabad", "Pune")

    // Pulsing animation for sensor status dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sensor")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
    ) {
        // Offline Sync Notice (if any)
        if (!isOnline || pendingSyncCount > 0) {
            Surface(
                color = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = Color(0xFF92400E),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!isOnline) "Offline Mode • Syncing when connected" else "$pendingSyncCount reports queued for sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                    if (pendingSyncCount > 0 && isOnline) {
                        TextButton(onClick = { viewModel.runManualSync() }) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = PolishPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Now", color = PolishPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Prominent "Sensor Detection Active" Card Banner
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Surface(
                color = PolishPrimaryContainer,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, PolishBorderHighlight),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(pulseAlpha)
                                    .background(PolishHazard, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sensor Detection Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (sensorDetections.isNotEmpty()) {
                                "${sensorDetections.size} new road anomalies detected while driving near HSR Layout."
                            } else {
                                "Auto-logging road bumps and vibration shocks while driving."
                            },
                            fontSize = 11.sp,
                            color = PolishTextSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (sensorDetections.isNotEmpty()) {
                                viewModel.showReviewSensorDialog.value = true
                            } else {
                                viewModel.setTab(AppTab.AUTO_SPOTTER)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("banner_review_btn")
                    ) {
                        Text(
                            text = if (sensorDetections.isNotEmpty()) "Review (${sensorDetections.size})" else "Review",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search & View Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = {
                    Text(
                        AppStrings.get("search_placeholder", currentLanguage),
                        fontSize = 13.sp,
                        color = PolishTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PolishPrimary
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PolishPrimary,
                    unfocusedBorderColor = PolishBorder,
                    focusedContainerColor = PolishSurface,
                    unfocusedContainerColor = PolishSurface
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_potholes_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Switch List / Map View
            Surface(
                color = PolishSurfaceVariant,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, PolishBorder)
            ) {
                IconButton(
                    onClick = {
                        viewModel.setViewMode(
                            if (viewMode == ViewMode.CARDS_LIST) ViewMode.RADAR_MAP else ViewMode.CARDS_LIST
                        )
                    },
                    modifier = Modifier.testTag("toggle_view_mode_btn")
                ) {
                    Icon(
                        imageVector = if (viewMode == ViewMode.CARDS_LIST) Icons.Default.Radar else Icons.Default.List,
                        contentDescription = "Toggle View",
                        tint = PolishPrimary
                    )
                }
            }
        }

        // Filter Chips Row (Cities)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cities.forEach { city ->
                val isSelected = selectedCity == city
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedCityFilter.value = city },
                    label = { Text(city, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PolishPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = PolishSurfaceVariant,
                        labelColor = PolishTextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) PolishPrimary else PolishBorder
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Content Area: List vs Radar Map
        if (viewMode == ViewMode.RADAR_MAP) {
            RadarMapView(
                reports = filteredReports,
                currentLanguage = currentLanguage,
                onSelectReport = { report -> viewModel.openUpdateStatusDialog(report) }
            )
        } else {
            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = PolishTextSecondary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No potholes match current search filters.",
                            fontWeight = FontWeight.SemiBold,
                            color = PolishTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECENT REPORTS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishTextPrimary,
                                letterSpacing = 0.8.sp
                            )
                            TextButton(
                                onClick = { viewModel.selectedCityFilter.value = "All India" },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "View Local Activity",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimary
                                )
                            }
                        }
                    }

                    items(filteredReports, key = { it.id }) { report ->
                        PotholeCard(
                            report = report,
                            currentLanguage = currentLanguage,
                            onUpvote = { viewModel.upvoteReport(it) },
                            onOpenUpdateStatus = { viewModel.openUpdateStatusDialog(it) },
                            onShare = { /* Share intent */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

