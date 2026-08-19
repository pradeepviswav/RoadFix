package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.components.DuplicatePotholeDialog
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.components.ReviewSensorDetectionsDialog
import com.example.ui.components.UpdateStatusDialog
import com.example.ui.screens.DriveSensorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyReportsScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportPotholeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val currentLanguage by viewModel.currentLanguage.collectAsState()
                val unreadNotifCount by viewModel.unreadNotifCount.collectAsState(initial = 0)
                val sensorTelemetry by viewModel.sensorTelemetry.collectAsState()
                val isOnline by viewModel.isOnline.collectAsState()

                // Dialog States
                val activeUpdatingReport by viewModel.activeUpdatingReport.collectAsState()
                val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
                val showDuplicateDialog by viewModel.showDuplicateDialog.collectAsState()
                val nearbyDuplicates by viewModel.nearbyDuplicates.collectAsState()
                val showReviewSensorDialog by viewModel.showReviewSensorDialog.collectAsState()
                val sensorDetections by viewModel.sensorDetections.collectAsState(initial = emptyList())

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(PolishPrimary, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "RC",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    androidx.compose.foundation.layout.Column {
                                        Text(
                                            text = "RoadCare",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = PolishNavy,
                                            letterSpacing = (-0.2).sp
                                        )
                                        Text(
                                            text = "INDIA UNIFIED • SADAK RAKSHAK",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PolishPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Language Quick Selector Pill with Divider
                                Surface(
                                    color = Color(0xFFEEF0F6),
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDE2EA)),
                                    modifier = Modifier
                                        .clickable { viewModel.showLanguageDialog.value = true }
                                        .testTag("top_language_pill")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentLanguage.code.uppercase(),
                                            color = PolishNavy,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .size(width = 1.dp, height = 10.dp)
                                                .background(Color(0xFFCBD5E1))
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = currentLanguage.nativeName.take(3),
                                            color = PolishTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // User Avatar Circle
                                Surface(
                                    color = Color(0xFFD1E4FF),
                                    shape = CircleShape,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { viewModel.setTab(AppTab.PROFILE) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "R",
                                            color = PolishNavy,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = PolishBackground
                            )
                        )
                    },
                    bottomBar = {
                        Surface(
                            color = PolishBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PolishBorder),
                            shadowElevation = 8.dp
                        ) {
                            NavigationBar(
                                containerColor = PolishBackground,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == AppTab.ROAD_RADAR,
                                    onClick = { viewModel.setTab(AppTab.ROAD_RADAR) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.ROAD_RADAR) Icons.Default.Radar else Icons.Outlined.Radar,
                                            contentDescription = "Radar"
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_road_radar", currentLanguage), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PolishPrimary,
                                        selectedTextColor = PolishPrimary,
                                        unselectedIconColor = PolishTextSecondary,
                                        unselectedTextColor = PolishTextSecondary,
                                        indicatorColor = Color(0xFFD1E4FF).copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("tab_radar")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.AUTO_SPOTTER,
                                    onClick = { viewModel.setTab(AppTab.AUTO_SPOTTER) },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (sensorTelemetry.tripPotholeCount > 0) {
                                                    Badge(containerColor = PolishHazard) {
                                                        Text("${sensorTelemetry.tripPotholeCount}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.AUTO_SPOTTER) Icons.Default.DirectionsCar else Icons.Outlined.DirectionsCar,
                                                contentDescription = "Drive"
                                            )
                                        }
                                    },
                                    label = { Text(AppStrings.get("tab_auto_spotter", currentLanguage), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PolishPrimary,
                                        selectedTextColor = PolishPrimary,
                                        unselectedIconColor = PolishTextSecondary,
                                        unselectedTextColor = PolishTextSecondary,
                                        indicatorColor = Color(0xFFD1E4FF).copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("tab_auto_spotter")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.REPORT_ISSUE,
                                    onClick = { viewModel.setTab(AppTab.REPORT_ISSUE) },
                                    icon = {
                                        Surface(
                                            color = PolishPrimary,
                                            shape = RoundedCornerShape(14.dp),
                                            shadowElevation = 4.dp,
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Report",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    },
                                    label = { Text(AppStrings.get("tab_report_issue", currentLanguage), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedTextColor = PolishPrimary,
                                        unselectedTextColor = PolishTextSecondary,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("tab_report")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.NOTIFICATIONS,
                                    onClick = { viewModel.setTab(AppTab.NOTIFICATIONS) },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (unreadNotifCount > 0) {
                                                    Badge(containerColor = PolishHazard) {
                                                        Text("$unreadNotifCount")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.NOTIFICATIONS) Icons.Default.Notifications else Icons.Outlined.Notifications,
                                                contentDescription = "Alerts"
                                            )
                                        }
                                    },
                                    label = { Text(AppStrings.get("alerts_title", currentLanguage).take(6), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PolishPrimary,
                                        selectedTextColor = PolishPrimary,
                                        unselectedIconColor = PolishTextSecondary,
                                        unselectedTextColor = PolishTextSecondary,
                                        indicatorColor = Color(0xFFD1E4FF).copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("tab_alerts")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.MY_REPORTS,
                                    onClick = { viewModel.setTab(AppTab.MY_REPORTS) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.MY_REPORTS) Icons.Default.Assignment else Icons.Outlined.Assignment,
                                            contentDescription = "Status"
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_my_reports", currentLanguage), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PolishPrimary,
                                        selectedTextColor = PolishPrimary,
                                        unselectedIconColor = PolishTextSecondary,
                                        unselectedTextColor = PolishTextSecondary,
                                        indicatorColor = Color(0xFFD1E4FF).copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("tab_my_reports")
                                )
                            }
                        }
                    },

                    floatingActionButton = {
                        if (currentTab == AppTab.ROAD_RADAR) {
                            FloatingActionButton(
                                onClick = { viewModel.setTab(AppTab.REPORT_ISSUE) },
                                containerColor = PolishPrimary,
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                                modifier = Modifier.testTag("fab_report_pothole")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Report Pothole")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Report Hazard",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppTab.ROAD_RADAR -> HomeScreen(viewModel = viewModel)
                            AppTab.REPORT_ISSUE -> ReportPotholeScreen(viewModel = viewModel)
                            AppTab.AUTO_SPOTTER -> DriveSensorScreen(viewModel = viewModel)
                            AppTab.MY_REPORTS -> MyReportsScreen(viewModel = viewModel)
                            AppTab.NOTIFICATIONS -> NotificationScreen(viewModel = viewModel)
                            AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }

                // Modals & Dialogs Handling
                activeUpdatingReport?.let { report ->
                    UpdateStatusDialog(
                        report = report,
                        currentLanguage = currentLanguage,
                        onDismiss = { viewModel.dismissUpdateStatusDialog() },
                        onConfirmUpdate = { status, note, photo ->
                            viewModel.submitStatusUpdate(status, note, photo)
                        }
                    )
                }

                if (showLanguageDialog) {
                    LanguagePickerDialog(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onDismiss = { viewModel.showLanguageDialog.value = false }
                    )
                }

                if (showDuplicateDialog && nearbyDuplicates.isNotEmpty()) {
                    val match = nearbyDuplicates.first()
                    DuplicatePotholeDialog(
                        matchedReport = match.existingReport,
                        distanceMeters = match.distanceMeters.toInt(),
                        currentLanguage = currentLanguage,
                        onUpvoteExisting = {
                            viewModel.upvoteReport(match.existingReport.id)
                            viewModel.showDuplicateDialog.value = false
                            viewModel.setTab(AppTab.ROAD_RADAR)
                        },
                        onProceedAnyway = {
                            viewModel.showDuplicateDialog.value = false
                        },
                        onDismiss = { viewModel.showDuplicateDialog.value = false }
                    )
                }

                if (showReviewSensorDialog) {
                    ReviewSensorDetectionsDialog(
                        detections = sensorDetections,
                        currentLanguage = currentLanguage,
                        onConfirmPost = { viewModel.convertSensorDetectionToReport(it) },
                        onDismissItem = { viewModel.deleteSensorItem(it) },
                        onDismiss = { viewModel.showReviewSensorDialog.value = false }
                    )
                }
            }
        }
    }
}

