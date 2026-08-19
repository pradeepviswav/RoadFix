package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SensorDetectedPothole
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDetectionDao {
    @Query("SELECT * FROM sensor_detections ORDER BY timestamp DESC")
    fun getAllDetections(): Flow<List<SensorDetectedPothole>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detection: SensorDetectedPothole)

    @Query("DELETE FROM sensor_detections WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM sensor_detections")
    suspend fun clearAll()
}
