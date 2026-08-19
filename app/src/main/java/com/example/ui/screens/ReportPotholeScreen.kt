package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PotholeSeverity
import com.example.data.model.RoadType
import com.example.localization.AppStrings
import com.example.ui.components.PotholeVisualGraphic
import com.example.ui.components.SeverityChip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPotholeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()

    val draftTitle by viewModel.reportDraftTitle.collectAsState()
    val draftDesc by viewModel.reportDraftDesc.collectAsState()
    val draftLocality by viewModel.reportDraftLocality.collectAsState()
    val draftLandmark by viewModel.reportDraftLandmark.collectAsState()
    val draftCity by viewModel.reportDraftCity.collectAsState()
    val draftState by viewModel.reportDraftState.collectAsState()
    val draftLat by viewModel.reportDraftLat.collectAsState()
    val draftLon by viewModel.reportDraftLon.collectAsState()
    val draftRoadType by viewModel.reportDraftRoadType.collectAsState()
    val draftSeverity by viewModel.reportDraftSeverity.collectAsState()
    val draftAuthority by viewModel.reportDraftAuthority.collectAsState()
    val draftPhotoLabel by viewModel.reportDraftPhotoLabel.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    var roadTypeExpanded by remember { mutableStateOf(false) }

    val presetLocations = listOf(
        Triple("Outer Ring Road, Bengaluru", "Near Marathahalli Bridge", "BBMP East Zone (Bengaluru)"),
        Triple("Western Express Highway, Mumbai", "Opposite Andheri Metro Station", "BMC K-East / MMRDA"),
        Triple("Ring Road Nizamuddin, Delhi", "Near Sarai Kale Khan Terminal", "Delhi PWD Central Division"),
        Triple("Anna Salai (Mount Rd), Chennai", "Near Thousand Lights Mosque", "Greater Chennai Corporation (GCC)"),
        Triple("Hitec City Main Road, Hyderabad", "Near Cyber Towers Junction", "GHMC Serilingampally Zone"),
        Triple("FC Road, Deccan Gymkhana, Pune", "Near Goodluck Chowk", "Pune Municipal Corporation (PMC)")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.get("report_pothole_title", currentLanguage),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PolishTextPrimary
        )
        Text(
            text = "Geotag road craters & damages for civic authority resolution.",
            fontSize = 12.sp,
            color = PolishTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Photo Capture & Preset Samples
        Card(
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PolishBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Pothole / Road Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Visual Representation Graphic
                PotholeVisualGraphic(
                    severity = draftSeverity,
                    isRepaired = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sample Photo Quick Chooser
                Text(
                    text = "Select Photo Condition / Capture:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PolishTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Deep Asphalt Crater",
                        "Waterlogged Trench",
                        "Edge Crack / Collapse"
                    ).forEach { label ->
                        val isSelected = draftPhotoLabel == label
                        Surface(
                            color = if (isSelected) PolishPrimaryContainer else PolishSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isSelected) PolishPrimary else PolishBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.reportDraftPhotoLabel.value = label
                                    val mockBmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(mockBmp)
                                    canvas.drawColor(android.graphics.Color.DKGRAY)
                                    val paint = Paint().apply { color = android.graphics.Color.BLACK }
                                    canvas.drawCircle(100f, 100f, 60f, paint)
                                    viewModel.reportDraftPhoto.value = mockBmp
                                    viewModel.runAiVerificationOnDraft()
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PolishPrimary else PolishTextPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI Verification Trigger Button
                FilledTonalButton(
                    onClick = { viewModel.runAiVerificationOnDraft() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_verify_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PolishPrimaryContainer
                    )
                ) {
                    if (isAiAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PolishPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            AppStrings.get("ai_verifying", currentLanguage),
                            color = PolishPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI Verify Road Damage with Gemini",
                            color = PolishPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // AI Result Card
                aiResult?.let { result ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = PolishPrimaryContainer,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PolishPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Verified: Pothole Detected",
                                        fontWeight = FontWeight.Bold,
                                        color = PolishNavy,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = "${(result.confidence * 100).toInt()}% Confidence",
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.damageDescription,
                                fontSize = 11.sp,
                                color = PolishTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚠️ ${result.roadSafetyRisk}",
                                fontSize = 11.sp,
                                color = PolishHazard,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 2: Geotagged Location & Road Classification
        Card(
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PolishBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. Geotag & Road Classification",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // GPS Location details
                OutlinedTextField(
                    value = draftLocality,
                    onValueChange = {
                        viewModel.reportDraftLocality.value = it
                        viewModel.checkProximityDuplicates(draftLat, draftLon)
                    },
                    label = { Text("Locality / Road Name", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locality_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = PolishBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = draftLandmark,
                    onValueChange = { viewModel.reportDraftLandmark.value = it },
                    label = { Text("Landmark / Pillar / Junction", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = PolishBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // City Presets
                Text(
                    text = "Quick Indian City Locality Presets:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PolishTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetLocations.take(3).forEach { (loc, mark, auth) ->
                        val cityName = loc.substringAfterLast(", ")
                        Surface(
                            color = PolishSurfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, PolishBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.selectPresetCityLocation(
                                        city = cityName,
                                        state = if (cityName == "Bengaluru") "Karnataka" else if (cityName == "Mumbai") "Maharashtra" else "Delhi",
                                        locality = loc,
                                        lat = 12.95 + (Math.random() * 0.05),
                                        lon = 77.65 + (Math.random() * 0.05),
                                        auth = auth
                                    )
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = cityName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PolishTextPrimary,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Road Classification Dropdown
                ExposedDropdownMenuBox(
                    expanded = roadTypeExpanded,
                    onExpandedChange = { roadTypeExpanded = !roadTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = draftRoadType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("road_type_label", currentLanguage), fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roadTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PolishPrimary,
                            unfocusedBorderColor = PolishBorder
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = roadTypeExpanded,
                        onDismissRequest = { roadTypeExpanded = false }
                    ) {
                        RoadType.entries.forEach { rt ->
                            DropdownMenuItem(
                                text = { Text(rt.displayName, fontSize = 13.sp) },
                                onClick = {
                                    viewModel.reportDraftRoadType.value = rt
                                    roadTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Assigned Authority Readout
                Surface(
                    color = PolishSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PolishBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Assigned Civic Agency:",
                            fontSize = 11.sp,
                            color = PolishTextSecondary
                        )
                        Text(
                            text = draftAuthority,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishNavy
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 3: Description & Submit
        Card(
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PolishBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3. Additional Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = draftTitle,
                    onValueChange = { viewModel.reportDraftTitle.value = it },
                    label = { Text("Issue Title", fontSize = 12.sp) },
                    placeholder = { Text("e.g. Deep Crater near Metro Pillar", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = PolishBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = draftDesc,
                    onValueChange = { viewModel.reportDraftDesc.value = it },
                    label = { Text("Detailed Road Description & Risk Factors", fontSize = 12.sp) },
                    placeholder = { Text("Waterlogging, two-wheeler swerving hazard, broken asphalt...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = PolishBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.submitPotholeReport() },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_pothole_report_btn")
                ) {
                    Text(
                        text = if (isOnline) AppStrings.get("submit_report", currentLanguage) else AppStrings.get("submit_offline_saved", currentLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
