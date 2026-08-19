package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.SyncManager
import com.example.data.firebase.FirebaseManager
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.data.model.PotholeReport
import com.example.data.model.ReportStatus
import com.example.data.model.SensorDetectedPothole
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PotholeRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    val potholeDao = database.potholeDao()
    val sensorDao = database.sensorDetectionDao()
    val notificationDao = database.notificationDao()
    val syncManager = SyncManager(context)
    val firebaseManager = FirebaseManager(context)

    val allReports: Flow<List<PotholeReport>> = potholeDao.getAllReports()
    val sensorDetections: Flow<List<SensorDetectedPothole>> = sensorDao.getAllDetections()
    val notifications: Flow<List<AppNotification>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    init {
        // Start listening for realtime multi-device updates from Firebase Firestore
        firebaseManager.startRealtimeSync { remoteReports ->
            CoroutineScope(Dispatchers.IO).launch {
                for (remote in remoteReports) {
                    potholeDao.insert(remote)
                }
            }
        }
    }

    suspend fun createReport(
        report: PotholeReport,
        isOnline: Boolean,
        photoBitmap: Bitmap? = null
    ) = withContext(Dispatchers.IO) {
        var finalPhotoUrl = report.imageUrl

        // Upload photo to Firebase Storage if online and photo provided
        if (isOnline && photoBitmap != null) {
            val uploadedUrl = firebaseManager.uploadPhotoToStorage(photoBitmap, report.id, isRepairProof = false)
            if (uploadedUrl != null) {
                finalPhotoUrl = uploadedUrl
            }
        }

        val finalReport = report.copy(imageUrl = finalPhotoUrl, isSyncPending = !isOnline)
        potholeDao.insert(finalReport)

        if (isOnline) {
            firebaseManager.uploadReportToFirestore(finalReport)
        }

        val notifTitle = if (isOnline) "Report Registered: ${report.title}" else "Report Saved (Offline Mode)"
        val notifMsg = if (isOnline) {
            "Assigned to ${report.authorityAssigned}. Tracking ref: #${report.id}"
        } else {
            "Report queued locally. Will automatically sync once network is restored."
        }

        val notif = NotificationHelper.createNotificationObject(
            title = notifTitle,
            message = notifMsg,
            reportId = report.id,
            type = NotificationType.STATUS_UPDATE
        )
        notificationDao.insert(notif)
        NotificationHelper.showPushNotification(context, notifTitle, notifMsg, report.id, NotificationType.STATUS_UPDATE)
    }

    suspend fun updateReportStatus(
        reportId: String,
        newStatus: ReportStatus,
        repairNote: String?,
        repairImageUrl: String?
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        val notifTitle: String
        val notifMsg: String
        val notifType: NotificationType

        when (newStatus) {
            ReportStatus.WORK_IN_PROGRESS -> {
                notifTitle = "Work Started: Road Repair Underway"
                notifMsg = "Civic road maintenance team has deployed equipment to fix pothole #$reportId."
                notifType = NotificationType.WORK_STARTED
            }
            ReportStatus.RESOLVED -> {
                notifTitle = "Road Fixed & Repaired! 🎉"
                notifMsg = "Repair completed for report #$reportId. Citizen inspection & confirmation open."
                notifType = NotificationType.RESOLVED
            }
            ReportStatus.UNDER_REVIEW -> {
                notifTitle = "Report Under Review"
                notifMsg = "Municipal Corporation engineers are reviewing pothole #$reportId."
                notifType = NotificationType.STATUS_UPDATE
            }
            ReportStatus.REPORTED -> {
                notifTitle = "Status Reset to Reported"
                notifMsg = "Report #$reportId marked as reported."
                notifType = NotificationType.STATUS_UPDATE
            }
        }

        database.openHelper.writableDatabase.execSQL(
            "UPDATE pothole_reports SET status = ?, repairNote = ?, repairImageUrl = ?, updatedTimestamp = ? WHERE id = ?",
            arrayOf(newStatus.name, repairNote, repairImageUrl, now, reportId)
        )

        // Sync update to Firebase Firestore
        firebaseManager.updateFirestoreReportStatus(reportId, newStatus, repairNote, repairImageUrl)

        val notif = NotificationHelper.createNotificationObject(notifTitle, notifMsg, reportId, notifType)
        notificationDao.insert(notif)
        NotificationHelper.showPushNotification(context, notifTitle, notifMsg, reportId, notifType)
    }

    suspend fun upvoteReport(reportId: String) = withContext(Dispatchers.IO) {
        potholeDao.upvoteReport(reportId)
        firebaseManager.upvoteReportInFirestore(reportId)
    }

    suspend fun syncPendingReports(): Int = withContext(Dispatchers.IO) {
        syncManager.setSyncing(true)
        val pending = potholeDao.getPendingSyncReports()
        for (report in pending) {
            val success = firebaseManager.uploadReportToFirestore(report)
            if (success) {
                potholeDao.markSynced(report.id)
            }
        }
        syncManager.setSyncing(false)

        if (pending.isNotEmpty()) {
            val title = "Data Synchronized (${pending.size} Reports)"
            val msg = "All offline road damage reports have been pushed to the central Firebase cloud backend."
            val notif = NotificationHelper.createNotificationObject(title, msg, "", NotificationType.STATUS_UPDATE)
            notificationDao.insert(notif)
            NotificationHelper.showPushNotification(context, title, msg, "", NotificationType.STATUS_UPDATE)
        }
        return@withContext pending.size
    }

    suspend fun saveSensorDetection(detection: SensorDetectedPothole) = withContext(Dispatchers.IO) {
        sensorDao.insert(detection)
    }

    suspend fun deleteSensorDetection(id: String) = withContext(Dispatchers.IO) {
        sensorDao.delete(id)
    }

    suspend fun clearAllSensorDetections() = withContext(Dispatchers.IO) {
        sensorDao.clearAll()
    }

    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }
}
