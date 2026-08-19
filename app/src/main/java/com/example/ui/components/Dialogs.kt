package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PotholeReport
import com.example.data.model.ReportStatus
import com.example.data.model.SensorDetectedPothole
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.OnPolishSuccessContainer
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishHazard
import com.example.ui.theme.PolishNavy
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryContainer
import com.example.ui.theme.PolishSuccess
import com.example.ui.theme.PolishSuccessContainer
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.PolishSurfaceVariant
import com.example.ui.theme.PolishTextPrimary
import com.example.ui.theme.PolishTextSecondary

@Composable
fun UpdateStatusDialog(
    report: PotholeReport,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmUpdate: (ReportStatus, String, String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(report.status) }
    var repairNote by remember { mutableStateOf(report.repairNote ?: "") }
    var hasAttachedRepairPhoto by remember { mutableStateOf(report.repairImageUrl != null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("update_status_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("update_repair_status", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PolishTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PolishTextSecondary)
                    }
                }

                Text(
                    text = "Ref: ${report.title}",
                    fontSize = 12.sp,
                    color = PolishTextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select New Progress Stage:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = PolishTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                listOf(
                    ReportStatus.UNDER_REVIEW to "Under Municipal Review",
                    ReportStatus.WORK_IN_PROGRESS to "Work Started / Asphalt Crew On-Site",
                    ReportStatus.RESOLVED to "Repaired & Completed (Road Restored)"
                ).forEach { (status, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 3.dp)
                    ) {
                        RadioButton(
                            selected = (selectedStatus == status),
                            onClick = { selectedStatus = status },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PolishPrimary,
                                unselectedColor = PolishBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedStatus == status) PolishPrimary else PolishTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = repairNote,
                    onValueChange = { repairNote = it },
                    label = { Text("Repair Progress Note / Agency Remarks", fontSize = 12.sp) },
                    placeholder = { Text("e.g. BBMP asphalt patch applied, leveling done...", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repair_note_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = PolishBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = if (hasAttachedRepairPhoto) PolishSuccessContainer else PolishSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (hasAttachedRepairPhoto) PolishSuccess.copy(alpha = 0.3f) else PolishBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasAttachedRepairPhoto = !hasAttachedRepairPhoto }
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasAttachedRepairPhoto) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (hasAttachedRepairPhoto) PolishSuccess else PolishPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (hasAttachedRepairPhoto) "Repair Proof Photo Attached ✓" else "Attach Road Repair Photo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (hasAttachedRepairPhoto) OnPolishSuccessContainer else PolishTextPrimary
                            )
                            Text(
                                text = if (hasAttachedRepairPhoto) "Live repair evidence ready to notify residents" else "Tap to attach after-repair inspection photo",
                                fontSize = 10.sp,
                                color = if (hasAttachedRepairPhoto) OnPolishSuccessContainer else PolishTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = PolishTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val photo = if (hasAttachedRepairPhoto) "repair_proof_img" else null
                            onConfirmUpdate(selectedStatus, repairNote.ifBlank { "Status updated to ${selectedStatus.label}" }, photo)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("submit_status_update_btn")
                    ) {
                        Text("Save & Broadcast", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("language_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = PolishPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select Language / भाषा चुनें",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PolishTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.height(340.dp)) {
                    items(AppLanguage.entries) { lang ->
                        val isSelected = lang == currentLanguage
                        Surface(
                            color = if (isSelected) PolishPrimaryContainer else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.3f)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(lang)
                                    onDismiss()
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = lang.nativeName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) PolishPrimary else PolishTextPrimary
                                    )
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 11.sp,
                                        color = PolishTextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PolishPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Done", color = PolishPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicatePotholeDialog(
    matchedReport: PotholeReport,
    distanceMeters: Int,
    currentLanguage: AppLanguage,
    onUpvoteExisting: () -> Unit,
    onProceedAnyway: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = PolishHazard,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("duplicate_warning_title", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PolishTextPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "AI Proximity Detection found an existing complaint just ${distanceMeters}m away:",
                    fontSize = 12.sp,
                    color = PolishTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = PolishSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PolishBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = matchedReport.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PolishTextPrimary
                        )
                        Text(
                            text = "${matchedReport.address} • Status: ${matchedReport.status.label}",
                            fontSize = 11.sp,
                            color = PolishTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upvoting the existing report helps city authorities escalate maintenance faster and prevents clutter.",
                    fontSize = 11.sp,
                    color = PolishTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpvoteExisting()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Upvote Existing (+1)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onProceedAnyway()
                onDismiss()
            }) {
                Text("File New Pothole", color = PolishTextSecondary)
            }
        }
    )
}

@Composable
fun ReviewSensorDetectionsDialog(
    detections: List<SensorDetectedPothole>,
    currentLanguage: AppLanguage,
    onConfirmPost: (SensorDetectedPothole) -> Unit,
    onDismissItem: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = PolishPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-Logged Potholes (${detections.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PolishTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PolishTextSecondary)
                    }
                }

                Text(
                    text = "Spotted via Accelerometer & Gyroscope during your drive. Confirm to file with city authorities.",
                    fontSize = 11.sp,
                    color = PolishTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (detections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending sensor detections.",
                            color = PolishTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(detections) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = PolishSurfaceVariant),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, PolishBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Impact: ${String.format("%.1f", item.gForceSpike)}G Shock",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = PolishPrimary
                                        )
                                        Text(
                                            text = item.estimatedSeverity.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PolishTextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.addressApprox,
                                        fontSize = 11.sp,
                                        color = PolishTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { onDismissItem(item.id) }) {
                                            Text("Discard", fontSize = 11.sp, color = PolishTextSecondary)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        FilledTonalButton(
                                            onClick = {
                                                onConfirmPost(item)
                                                onDismissItem(item.id)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Post to Platform", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = PolishPrimary)
                    }
                }
            }
        }
    }
}
