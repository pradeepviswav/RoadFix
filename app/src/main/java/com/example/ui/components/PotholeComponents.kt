package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.OnPolishHazardContainer
import com.example.ui.theme.OnPolishSuccessContainer
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishHazardContainer
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryContainer
import com.example.ui.theme.PolishSuccess
import com.example.ui.theme.PolishSuccessContainer
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishSurfaceVariant
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary
import com.example.ui.theme.PolishWarning
import com.example.ui.theme.PolishWarningContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SeverityChip(severity: PotholeSeverity, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (severity) {
        PotholeSeverity.MINOR -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Minor Dip")
        PotholeSeverity.MODERATE -> Triple(PolishWarningContainer, Color(0xFF805600), "Moderate")
        PotholeSeverity.SEVERE -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "Severe Crater")
        PotholeSeverity.CRITICAL_HAZARD -> Triple(PolishHazardContainer, OnPolishHazardContainer, "Critical Hazard")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(status: ReportStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        ReportStatus.REPORTED -> Triple(PolishHazardContainer, OnPolishHazardContainer, "REPORTED")
        ReportStatus.UNDER_REVIEW -> Triple(PolishWarningContainer, Color(0xFF281800), "UNDER REVIEW")
        ReportStatus.WORK_IN_PROGRESS -> Triple(PolishPrimaryContainer, PolishNavy, "IN PROGRESS")
        ReportStatus.RESOLVED -> Triple(PolishSuccessContainer, OnPolishSuccessContainer, "FIXED")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun PotholeCard(
    report: PotholeReport,
    currentLanguage: AppLanguage,
    onUpvote: (String) -> Unit,
    onOpenUpdateStatus: (PotholeReport) -> Unit,
    onShare: (PotholeReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDetails by remember { mutableStateOf(false) }
    val dateStr = remember(report.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date(report.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expandedDetails = !expandedDetails }
            .testTag("pothole_card_${report.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PolishSurface),
        border = BorderStroke(1.dp, PolishBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Photo Thumbnail Box
                Surface(
                    color = PolishSurfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PolishBorder),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Pothole Photo",
                            tint = PolishTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Core Details Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = report.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(status = report.status)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Authority: ${report.authorityAssigned}",
                        fontSize = 11.sp,
                        color = PolishTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Footer Row: Timestamp + AI Verified Pill + Cluster Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = PolishTextSecondary
                        )

                        if (report.aiVerified) {
                            Text(
                                text = "AI Verified ✓",
                                color = PolishPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (report.upvotesCount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = PolishTextSecondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Cluster: ${report.upvotesCount} Users",
                                    fontSize = 10.sp,
                                    color = PolishTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Expanded details section
            AnimatedVisibility(visible = expandedDetails) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        color = PolishSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PolishBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = report.description,
                                fontSize = 12.sp,
                                color = PolishTextPrimary,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📍 ${report.address}, ${report.city} (${report.roadType.displayName})",
                                fontSize = 11.sp,
                                color = PolishTextSecondary
                            )
                            if (report.aiNotes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🤖 AI Analysis: ${report.aiNotes}",
                                    fontSize = 11.sp,
                                    color = PolishPrimary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    if (report.repairNote != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = if (report.status == ReportStatus.RESOLVED) PolishSuccessContainer else PolishPrimaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (report.status == ReportStatus.RESOLVED) Icons.Default.CheckCircle else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (report.status == ReportStatus.RESOLVED) PolishSuccess else PolishPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = report.repairNote,
                                    fontSize = 11.sp,
                                    color = if (report.status == ReportStatus.RESOLVED) OnPolishSuccessContainer else PolishNavy
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onUpvote(report.id) },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (report.isUpvotedByMe) PolishPrimary else PolishBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (report.isUpvotedByMe) PolishPrimaryContainer else Color.Transparent
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("upvote_btn_${report.id}")
                        ) {
                            Icon(
                                imageVector = if (report.isUpvotedByMe) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                contentDescription = null,
                                tint = if (report.isUpvotedByMe) PolishPrimary else PolishTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${report.upvotesCount} +1 Hazard",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.isUpvotedByMe) PolishPrimary else PolishTextPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = { onOpenUpdateStatus(report) },
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("update_status_btn_${report.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.get("update_repair_status", currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(
                                onClick = { onShare(report) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = PolishTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PotholeVisualGraphic(
    severity: PotholeSeverity,
    isRepaired: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
    ) {
        // Road surface styling with dashed center line
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE2E8F0).copy(alpha = 0.6f))
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE2E8F0).copy(alpha = 0.6f))
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE2E8F0).copy(alpha = 0.6f))
            )
        }

        if (isRepaired) {
            Surface(
                color = PolishSuccess.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PolishSuccess),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PolishSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Road Surface Re-asphalted ✓",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Surface(
                color = PolishHazard.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PolishHazard),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = PolishHazard,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active Hazard: ${severity.name.replace("_", " ")}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
