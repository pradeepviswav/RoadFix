package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SadakRakshakApp
import com.example.ai.AIVerificationResult
import com.example.ai.PotholeAIVerifier
import com.example.auth.AuthManager
import com.example.data.model.AppNotification
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.data.model.RoadType
import com.example.data.model.SensorDetectedPothole
import com.example.data.model.UserProfile
import com.example.localization.AppLanguage
import com.example.sensor.PotholeSensorManager
import com.example.sensor.SensorTelemetry
import com.example.util.ProximityClustering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab {
    ROAD_RADAR,
    REPORT_ISSUE,
    AUTO_SPOTTER,
    MY_REPORTS,
    NOTIFICATIONS,
    PROFILE
}

enum class ViewMode {
    CARDS_LIST,
    RADAR_MAP
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as SadakRakshakApp).repository
    val authManager = AuthManager(application)
    val sensorManager = PotholeSensorManager(application)

    // Navigation & View State
    private val _currentTab = MutableStateFlow(AppTab.ROAD_RADAR)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.CARDS_LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    // Filters
    val searchQuery = MutableStateFlow("")
    val selectedCityFilter = MutableStateFlow("All India")
    val selectedStatusFilter = MutableStateFlow<ReportStatus?>(null)
    val selectedSeverityFilter = MutableStateFlow<PotholeSeverity?>(null)

    // Base Data Flows
    val allReports = repository.allReports
    val notifications = repository.notifications
    val unreadNotifCount = repository.unreadNotificationsCount
    val sensorDetections = repository.sensorDetections
    val isOnline = repository.syncManager.isOnline
    val isSimulatedOffline = repository.syncManager.isSimulatedOffline
    val isSyncing = repository.syncManager.isSyncing

    val currentUser: StateFlow<UserProfile> = authManager.currentUser
    val currentLanguage: StateFlow<AppLanguage> = authManager.currentLanguage
    val sensorTelemetry: StateFlow<SensorTelemetry> = sensorManager.telemetry

    // Filtered Reports Flow
    val filteredReports: StateFlow<List<PotholeReport>> = combine(
        allReports,
        searchQuery,
        selectedCityFilter,
        selectedStatusFilter,
        selectedSeverityFilter
    ) { reports, query, city, status, severity ->
        reports.filter { rep ->
            val matchesQuery = query.isBlank() ||
                    rep.title.contains(query, ignoreCase = true) ||
                    rep.address.contains(query, ignoreCase = true) ||
                    rep.city.contains(query, ignoreCase = true) ||
                    rep.landmark.contains(query, ignoreCase = true)

            val matchesCity = city == "All India" || rep.city.equals(city, ignoreCase = true)
            val matchesStatus = status == null || rep.status == status
            val matchesSeverity = severity == null || rep.severity == severity

            matchesQuery && matchesCity && matchesStatus && matchesSeverity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending Offline Sync count
    val pendingSyncCount: StateFlow<Int> = allReports.combine(MutableStateFlow(0)) { reports, _ ->
        reports.count { it.isSyncPending }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Report Issue Creation Draft State
    val reportDraftTitle = MutableStateFlow("")
    val reportDraftDesc = MutableStateFlow("")
    val reportDraftLocality = MutableStateFlow("Outer Ring Road, Bengaluru")
    val reportDraftLandmark = MutableStateFlow("Near Marathahalli Bridge")
    val reportDraftCity = MutableStateFlow("Bengaluru")
    val reportDraftState = MutableStateFlow("Karnataka")
    val reportDraftLat = MutableStateFlow(12.9569)
    val reportDraftLon = MutableStateFlow(77.7011)
    val reportDraftRoadType = MutableStateFlow(RoadType.URBAN_MAIN_ROAD)
    val reportDraftSeverity = MutableStateFlow(PotholeSeverity.SEVERE)
    val reportDraftAuthority = MutableStateFlow("BBMP East Zone (Bengaluru)")
    val reportDraftPhoto = MutableStateFlow<Bitmap?>(null)
    val reportDraftPhotoLabel = MutableStateFlow("Crater on asphalt lane")

    val isAiAnalyzing = MutableStateFlow(false)
    val aiResult = MutableStateFlow<AIVerificationResult?>(null)
    val nearbyDuplicates = MutableStateFlow<List<ProximityClustering.DuplicateMatch>>(emptyList())
    val showDuplicateDialog = MutableStateFlow(false)

    // Modals & Dialogs
    val activeUpdatingReport = MutableStateFlow<PotholeReport?>(null)
    val showLanguageDialog = MutableStateFlow(false)
    val showReviewSensorDialog = MutableStateFlow(false)

    init {
        // Wire auto-detector listener to save to Room
        sensorManager.onPotholeDetectedListener = { detection ->
            viewModelScope.launch {
                repository.saveSensorDetection(detection)
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setLanguage(lang: AppLanguage) {
        authManager.setLanguage(lang)
    }

    fun toggleOfflineSimulation() {
        val current = isSimulatedOffline.value
        repository.syncManager.toggleSimulatedOffline(!current)
    }

    fun runManualSync() {
        viewModelScope.launch {
            repository.syncPendingReports()
        }
    }

    fun upvoteReport(reportId: String) {
        viewModelScope.launch {
            repository.upvoteReport(reportId)
            authManager.addKarmaPoints(5)
        }
    }

    fun openUpdateStatusDialog(report: PotholeReport) {
        activeUpdatingReport.value = report
    }

    fun dismissUpdateStatusDialog() {
        activeUpdatingReport.value = null
    }

    fun submitStatusUpdate(newStatus: ReportStatus, note: String, photo: String?) {
        val report = activeUpdatingReport.value ?: return
        viewModelScope.launch {
            repository.updateReportStatus(report.id, newStatus, note, photo)
            authManager.addKarmaPoints(15)
            if (newStatus == ReportStatus.RESOLVED) {
                authManager.incrementReportsFixed()
            }
            activeUpdatingReport.value = null
        }
    }

    fun checkProximityDuplicates(lat: Double, lon: Double) {
        viewModelScope.launch {
            val list = allReports.first()
            val duplicates = ProximityClustering.findNearbyDuplicates(lat, lon, list, 150.0)
            nearbyDuplicates.value = duplicates
            if (duplicates.isNotEmpty()) {
                showDuplicateDialog.value = true
            }
        }
    }

    fun runAiVerificationOnDraft() {
        viewModelScope.launch {
            isAiAnalyzing.value = true
            val result = PotholeAIVerifier.verifyPotholeImage(
                bitmap = reportDraftPhoto.value,
                photoLabel = reportDraftPhotoLabel.value,
                roadTypeName = reportDraftRoadType.value.displayName,
                localityHint = "${reportDraftCity.value}, ${reportDraftState.value}"
            )
            aiResult.value = result
            reportDraftSeverity.value = result.estimatedSeverity
            reportDraftAuthority.value = result.recommendedAuthority
            isAiAnalyzing.value = false
        }
    }

    fun submitPotholeReport() {
        viewModelScope.launch {
            val newId = "REP-IND-" + (1000 + (System.currentTimeMillis() % 9000).toInt())
            val user = currentUser.value
            val ai = aiResult.value

            val report = PotholeReport(
                id = newId,
                title = reportDraftTitle.value.ifBlank { "Road Damage on ${reportDraftLocality.value}" },
                description = reportDraftDesc.value.ifBlank { "Reported via SadakRakshak mobile citizen app." },
                latitude = reportDraftLat.value,
                longitude = reportDraftLon.value,
                address = reportDraftLocality.value,
                landmark = reportDraftLandmark.value,
                city = reportDraftCity.value,
                state = reportDraftState.value,
                roadType = reportDraftRoadType.value,
                severity = reportDraftSeverity.value,
                status = ReportStatus.REPORTED,
                imageUrl = "pothole_user_capture",
                reportedByUserId = user.userId,
                reportedByUserName = user.name,
                timestamp = System.currentTimeMillis(),
                updatedTimestamp = System.currentTimeMillis(),
                upvotesCount = 1,
                isUpvotedByMe = true,
                aiVerified = ai?.isPotholeVerified ?: true,
                aiConfidence = ai?.confidence ?: 0.94f,
                aiNotes = ai?.damageDescription ?: "AI verified road hazard.",
                authorityAssigned = reportDraftAuthority.value
            )

            repository.createReport(report, isOnline.value, photoBitmap = reportDraftPhoto.value)
            authManager.addKarmaPoints(25)
            authManager.incrementReportsFiled()

            // Reset draft
            reportDraftTitle.value = ""
            reportDraftDesc.value = ""
            aiResult.value = null
            reportDraftPhoto.value = null

            // Switch to feed
            _currentTab.value = AppTab.ROAD_RADAR
        }
    }

    fun convertSensorDetectionToReport(detection: SensorDetectedPothole) {
        viewModelScope.launch {
            val newId = "REP-SEN-" + (1000 + (System.currentTimeMillis() % 9000).toInt())
            val user = currentUser.value

            val report = PotholeReport(
                id = newId,
                title = "Auto-Spotted Road Bump (${String.format("%.1f", detection.gForceSpike)}G Impact)",
                description = "Automated pothole detection logged while driving via smartphone Accelerometer & Gyroscope sensors.",
                latitude = detection.latitude,
                longitude = detection.longitude,
                address = detection.addressApprox,
                landmark = "Auto-Logged Geo Coordinate",
                city = "Bengaluru",
                state = "Karnataka",
                roadType = RoadType.URBAN_MAIN_ROAD,
                severity = detection.estimatedSeverity,
                status = ReportStatus.REPORTED,
                imageUrl = "sample_pothole_sensor",
                reportedByUserId = user.userId,
                reportedByUserName = user.name,
                timestamp = detection.timestamp,
                updatedTimestamp = detection.timestamp,
                upvotesCount = 1,
                isUpvotedByMe = true,
                aiVerified = true,
                aiConfidence = 0.96f,
                aiNotes = "Sensor Telemetry: Vertical G-Force spike of ${String.format("%.2f", detection.gForceSpike)}G confirmed.",
                authorityAssigned = "Municipal Road Infrastructure Division"
            )

            repository.createReport(report, isOnline.value)
            repository.deleteSensorDetection(detection.id)
            authManager.addKarmaPoints(30)
            authManager.incrementReportsFiled()
        }
    }

    fun deleteSensorItem(id: String) {
        viewModelScope.launch {
            repository.deleteSensorDetection(id)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun selectPresetCityLocation(city: String, state: String, locality: String, lat: Double, lon: Double, auth: String) {
        reportDraftCity.value = city
        reportDraftState.value = state
        reportDraftLocality.value = locality
        reportDraftLat.value = lat
        reportDraftLon.value = lon
        reportDraftAuthority.value = auth
        checkProximityDuplicates(lat, lon)
    }

    fun signInWithGoogle() {
        authManager.signInWithGoogle()
    }

    fun continueAsGuest() {
        authManager.continueAsGuest()
    }
}
