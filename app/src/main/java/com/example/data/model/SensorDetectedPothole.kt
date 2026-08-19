package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_detections")
data class SensorDetectedPothole(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val gForceSpike: Float,
    val gyroTilt: Float,
    val estimatedSeverity: PotholeSeverity,
    val addressApprox: String,
    val isConfirmed: Boolean = false
)
