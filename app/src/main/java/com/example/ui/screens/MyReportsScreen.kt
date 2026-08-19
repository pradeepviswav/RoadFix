package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.components.PotholeCard
import com.example.ui.theme.OnPolishSuccessContainer
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryContainer
import com.example.ui.theme.PolishSuccess
import com.example.ui.theme.PolishSuccessContainer
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishSurfaceVariant
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MyReportsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val allReports by viewModel.allReports.collectAsState(initial = emptyList())
    val currentUser by viewModel.currentUser.collectAsState()

    val myReports = allReports.filter { it.reportedByUserId == currentUser.userId || it.isUpvotedByMe }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = AppStrings.get("my_reports_title", currentLanguage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PolishTextPrimary
            )
            Text(
                text = "Track your filed complaints and monitor repair progress.",
                fontSize = 12.sp,
                color = PolishTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Karma / Impact Card
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
                                    .size(38.dp)
                                    .background(PolishPrimaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = PolishPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentUser.citizenRank,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishTextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${currentUser.name} • ${if (currentUser.isGuest) "Guest Citizen" else "Google Verified"}",
                                    color = PolishTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            color = PolishPrimaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = PolishPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentUser.karmaPoints} Karma",
                                    color = PolishPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatPill(
                            count = "${myReports.size}",
                            label = "Reports",
                            modifier = Modifier.weight(1f)
                        )
                        StatPill(
                            count = "${myReports.count { it.status == com.example.data.model.ReportStatus.WORK_IN_PROGRESS }}",
                            label = "In Progress",
                            modifier = Modifier.weight(1f)
                        )
                        StatPill(
                            count = "${myReports.count { it.status == com.example.data.model.ReportStatus.RESOLVED }}",
                            label = "Fixed",
                            isGreen = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Road Complaints (${myReports.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PolishTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (myReports.isEmpty()) {
            item {
                Surface(
                    color = PolishSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PolishBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = null,
                            tint = PolishTextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You haven't reported or upvoted any potholes yet.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PolishTextSecondary
                        )
                    }
                }
            }
        } else {
            items(myReports, key = { it.id }) { report ->
                PotholeCard(
                    report = report,
                    currentLanguage = currentLanguage,
                    onUpvote = { viewModel.upvoteReport(it) },
                    onOpenUpdateStatus = { viewModel.openUpdateStatusDialog(it) },
                    onShare = { /* Share */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StatPill(
    count: String,
    label: String,
    isGreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isGreen) PolishSuccessContainer else PolishSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isGreen) PolishSuccess.copy(alpha = 0.3f) else PolishBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGreen) OnPolishSuccessContainer else PolishNavy
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isGreen) OnPolishSuccessContainer else PolishTextSecondary
            )
        }
    }
}
