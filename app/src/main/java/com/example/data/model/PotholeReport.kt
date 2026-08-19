package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pothole_reports")
data class PotholeReport(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val landmark: String,
    val city: String,
    val state: String,
    val roadType: RoadType,
    val severity: PotholeSeverity,
    val status: ReportStatus,
    val imageUrl: String,
    val repairImageUrl: String? = null,
    val repairNote: String? = null,
    val reportedByUserId: String,
    val reportedByUserName: String,
    val timestamp: Long,
    val updatedTimestamp: Long,
    val upvotesCount: Int = 0,
    val isUpvotedByMe: Boolean = false,
    val aiVerified: Boolean = true,
    val aiConfidence: Float = 0.94f,
    val aiNotes: String = "AI Road Vision detected asphalt surface loss and risk to two-wheelers.",
    val isSyncPending: Boolean = false,
    val authorityAssigned: String = "Municipal Corporation"
)
