package com.example.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.data.model.RoadType
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.UUID

class FirebaseManager(private val context: Context) {

    private val tag = "FirebaseManager"
    private var firestoreListener: ListenerRegistration? = null

    private val isFirebaseAvailable: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    private val firestore: FirebaseFirestore?
        get() = if (isFirebaseAvailable) {
            try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
        } else null

    private val storage: FirebaseStorage?
        get() = if (isFirebaseAvailable) {
            try { FirebaseStorage.getInstance() } catch (e: Exception) { null }
        } else null

    private val auth: FirebaseAuth?
        get() = if (isFirebaseAvailable) {
            try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
        } else null

    /**
     * Start listening for realtime Firestore updates across all devices
     */
    fun startRealtimeSync(onReportsUpdated: (List<PotholeReport>) -> Unit) {
        val db = firestore ?: return
        try {
            firestoreListener?.remove()
            firestoreListener = db.collection("pothole_reports")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(tag, "Listen failed from Firestore: ${e.message}")
                        return@addSnapshotListener
                    }
                    if (snapshots != null && !snapshots.isEmpty) {
                        val reports = snapshots.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val title = doc.getString("title") ?: "Pothole Alert"
                                val description = doc.getString("description") ?: ""
                                val latitude = doc.getDouble("latitude") ?: 12.9716
                                val longitude = doc.getDouble("longitude") ?: 77.5946
                                val address = doc.getString("address") ?: "Bengaluru, Karnataka"
                                val landmark = doc.getString("landmark") ?: ""
                                val city = doc.getString("city") ?: "Bengaluru"
                                val state = doc.getString("state") ?: "Karnataka"
                                val roadTypeStr = doc.getString("roadType") ?: RoadType.URBAN_MAIN_ROAD.name
                                val severityStr = doc.getString("severity") ?: PotholeSeverity.SEVERE.name
                                val statusStr = doc.getString("status") ?: ReportStatus.REPORTED.name
                                val imageUrl = doc.getString("imageUrl") ?: "sample_pothole_1"
                                val repairImageUrl = doc.getString("repairImageUrl")
                                val repairNote = doc.getString("repairNote")
                                val reportedByUserId = doc.getString("reportedByUserId") ?: "anonymous"
                                val reportedByUserName = doc.getString("reportedByUserName") ?: "Citizen"
                                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                val updatedTimestamp = doc.getLong("updatedTimestamp") ?: timestamp
                                val upvotesCount = (doc.getLong("upvotesCount") ?: 0L).toInt()
                                val aiVerified = doc.getBoolean("aiVerified") ?: true
                                val aiConfidence = (doc.getDouble("aiConfidence") ?: 0.95).toFloat()
                                val aiNotes = doc.getString("aiNotes") ?: "AI Verified road surface crater."
                                val authorityAssigned = doc.getString("authorityAssigned") ?: "Municipal Corporation"

                                PotholeReport(
                                    id = id,
                                    title = title,
                                    description = description,
                                    latitude = latitude,
                                    longitude = longitude,
                                    address = address,
                                    landmark = landmark,
                                    city = city,
                                    state = state,
                                    roadType = try { RoadType.valueOf(roadTypeStr) } catch (ex: Exception) { RoadType.URBAN_MAIN_ROAD },
                                    severity = try { PotholeSeverity.valueOf(severityStr) } catch (ex: Exception) { PotholeSeverity.SEVERE },
                                    status = try { ReportStatus.valueOf(statusStr) } catch (ex: Exception) { ReportStatus.REPORTED },
                                    imageUrl = imageUrl,
                                    repairImageUrl = repairImageUrl,
                                    repairNote = repairNote,
                                    reportedByUserId = reportedByUserId,
                                    reportedByUserName = reportedByUserName,
                                    timestamp = timestamp,
                                    updatedTimestamp = updatedTimestamp,
                                    upvotesCount = upvotesCount,
                                    isUpvotedByMe = false,
                                    aiVerified = aiVerified,
                                    aiConfidence = aiConfidence,
                                    aiNotes = aiNotes,
                                    isSyncPending = false,
                                    authorityAssigned = authorityAssigned
                                )
                            } catch (parseEx: Exception) {
                                Log.e(tag, "Error parsing doc ${doc.id}", parseEx)
                                null
                            }
                        }
                        onReportsUpdated(reports)
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Failed to attach snapshot listener", e)
        }
    }

    fun stopRealtimeSync() {
        firestoreListener?.remove()
        firestoreListener = null
    }

    /**
     * Upload or sync a report to Firestore
     */
    suspend fun uploadReportToFirestore(report: PotholeReport): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val reportMap = hashMapOf(
                "id" to report.id,
                "title" to report.title,
                "description" to report.description,
                "latitude" to report.latitude,
                "longitude" to report.longitude,
                "address" to report.address,
                "landmark" to report.landmark,
                "city" to report.city,
                "state" to report.state,
                "roadType" to report.roadType.name,
                "severity" to report.severity.name,
                "status" to report.status.name,
                "imageUrl" to report.imageUrl,
                "repairImageUrl" to report.repairImageUrl,
                "repairNote" to report.repairNote,
                "reportedByUserId" to report.reportedByUserId,
                "reportedByUserName" to report.reportedByUserName,
                "timestamp" to report.timestamp,
                "updatedTimestamp" to report.updatedTimestamp,
                "upvotesCount" to report.upvotesCount,
                "aiVerified" to report.aiVerified,
                "aiConfidence" to report.aiConfidence.toDouble(),
                "aiNotes" to report.aiNotes,
                "authorityAssigned" to report.authorityAssigned
            )
            db.collection("pothole_reports").document(report.id).set(reportMap, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to upload report to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Update report status on Firestore
     */
    suspend fun updateFirestoreReportStatus(
        reportId: String,
        newStatus: ReportStatus,
        repairNote: String?,
        repairImageUrl: String?
    ): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val updates = hashMapOf<String, Any>(
                "status" to newStatus.name,
                "updatedTimestamp" to System.currentTimeMillis()
            )
            repairNote?.let { updates["repairNote"] = it }
            repairImageUrl?.let { updates["repairImageUrl"] = it }

            db.collection("pothole_reports").document(reportId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to update report status in Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Upvote a report in Firestore
     */
    suspend fun upvoteReportInFirestore(reportId: String): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            db.collection("pothole_reports").document(reportId)
                .update("upvotesCount", FieldValue.increment(1))
                .await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to upvote in Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Upload photo to Firebase Storage and retrieve downloadable HTTPS URL
     */
    suspend fun uploadPhotoToStorage(bitmap: Bitmap, reportId: String, isRepairProof: Boolean = false): String? = withContext(Dispatchers.IO) {
        val st = storage ?: return@withContext null
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            val data = stream.toByteArray()

            val photoType = if (isRepairProof) "repair_proof" else "incident"
            val path = "potholes/$reportId/${photoType}_${UUID.randomUUID().toString().take(6)}.jpg"
            val ref = st.reference.child(path)

            ref.putBytes(data).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            Log.e(tag, "Failed to upload photo to Firebase Storage: ${e.message}", e)
            null
        }
    }

    /**
     * Google Sign-In with Credential Manager
     */
    suspend fun signInWithGoogleCredential(
        activityContext: android.app.Activity,
        serverClientId: String? = null
    ): Result<Pair<String, String>> = withContext(Dispatchers.Main) {
        try {
            val credentialManager = CredentialManager.create(activityContext)
            val googleIdOption = GetSignInWithGoogleOption.Builder(
                serverClientId ?: "417937989345-gcvh6b033j5r8spg3m379j84c7i0v4p7.apps.googleusercontent.com"
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                val email = credential.id
                val displayName = credential.displayName ?: email.substringBefore("@")
                val idToken = credential.idToken

                // Authenticate with Firebase if auth available
                val fbAuth = auth
                if (fbAuth != null && idToken.isNotEmpty()) {
                    try {
                        val authCred = GoogleAuthProvider.getCredential(idToken, null)
                        fbAuth.signInWithCredential(authCred).await()
                    } catch (fbEx: Exception) {
                        Log.w(tag, "Firebase Auth credential sign-in note: ${fbEx.message}")
                    }
                }

                Result.success(Pair(displayName, email))
            } else {
                Result.failure(Exception("Unknown credential format received"))
            }
        } catch (e: GetCredentialException) {
            Log.w(tag, "Google Credential Manager handled: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(tag, "Google Sign-In failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
