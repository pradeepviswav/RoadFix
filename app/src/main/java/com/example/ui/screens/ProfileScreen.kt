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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isSimulatedOffline by viewModel.isSimulatedOffline.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.get("profile_title", currentLanguage),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PolishTextPrimary
        )
        Text(
            text = "Google OAuth authentication, language customization & offline sync.",
            fontSize = 12.sp,
            color = PolishTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account Profile Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(PolishPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.take(1).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PolishTextPrimary
                        )
                        Text(
                            text = currentUser.email,
                            fontSize = 12.sp,
                            color = PolishTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = if (currentUser.isGuest) PolishSurfaceVariant else PolishSuccessContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (currentUser.isGuest) "Guest Citizen Mode" else "Google OAuth Verified ✓",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser.isGuest) PolishTextSecondary else OnPolishSuccessContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Sign in with Google / Guest Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.signInWithGoogle() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!currentUser.isGuest) PolishPrimary else PolishPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("google_signin_btn")
                    ) {
                        Text(
                            text = AppStrings.get("sign_in_google", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!currentUser.isGuest) Color.White else PolishPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.continueAsGuest() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("guest_mode_btn")
                    ) {
                        Text(
                            text = AppStrings.get("guest_mode", currentLanguage),
                            fontSize = 11.sp,
                            color = PolishTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selector Card (12 Indian Languages)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.showLanguageDialog.value = true }
                .testTag("language_selector_card")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PolishPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppStrings.get("change_language", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PolishTextPrimary
                        )
                        Text(
                            text = "${currentLanguage.nativeName} (${currentLanguage.displayName})",
                            fontSize = 11.sp,
                            color = PolishPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "Change ›",
                    fontSize = 12.sp,
                    color = PolishPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Offline Mode & Room Sync Section
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Local Storage & Offline Synchronization",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When travelling in poor coverage zones, reports are cached in local Room database and synced automatically when connected.",
                    fontSize = 11.sp,
                    color = PolishTextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isOnline) PolishSuccess else PolishTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulate Offline Mode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PolishTextPrimary
                        )
                    }
                    Switch(
                        checked = isSimulatedOffline,
                        onCheckedChange = { viewModel.toggleOfflineSimulation() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PolishPrimary
                        ),
                        modifier = Modifier.testTag("toggle_sim_offline_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = PolishSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pending Offline Sync Queue:",
                                fontSize = 10.sp,
                                color = PolishTextSecondary
                            )
                            Text(
                                text = "$pendingSyncCount Reports Queued",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingSyncCount > 0) PolishPrimary else PolishSuccess
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.runManualSync() },
                            enabled = isOnline && pendingSyncCount > 0,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("force_sync_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Force Sync", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Firebase Cloud Backend & Multi-device Sharing Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Firebase Cloud & Multi-Device Sync",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PolishTextPrimary
                        )
                    }
                    Surface(
                        color = PolishSuccessContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnPolishSuccessContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Cloud Firestore: Realtime broadcast across all devices & municipal dashboards\n• Firebase Storage: Geotagged high-resolution road photos & repair proofs\n• Authentication: Google OAuth & Identity Credential Manager\n• Offline Fallback: Room database caching with background sync queue",
                    fontSize = 11.sp,
                    color = PolishTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Civic Authorities Assigned Directory
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurface),
            border = BorderStroke(1.dp, PolishBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏛️ Integrated Civic Agencies in India",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PolishTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• NHAI (National Highways Authority of India)\n• State PWD (Public Works Department)\n• BBMP (Bruhat Bengaluru Mahanagara Palike)\n• BMC / MMRDA (Mumbai Metropolitan Region)\n• Delhi PWD Road Maintenance Cell\n• GHMC (Greater Hyderabad Municipal Corp)\n• GCC (Greater Chennai Corporation)\n• PMC (Pune Municipal Corporation)",
                    fontSize = 11.sp,
                    color = PolishTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
