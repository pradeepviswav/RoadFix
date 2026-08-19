package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PotholeReport
import kotlinx.coroutines.flow.Flow

@Dao
interface PotholeDao {
    @Query("SELECT * FROM pothole_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<PotholeReport>>

    @Query("SELECT * FROM pothole_reports WHERE id = :id")
    fun getReportById(id: String): Flow<PotholeReport?>

    @Query("SELECT * FROM pothole_reports WHERE reportedByUserId = :userId ORDER BY timestamp DESC")
    fun getReportsByUser(userId: String): Flow<List<PotholeReport>>

    @Query("SELECT * FROM pothole_reports WHERE isSyncPending = 1")
    suspend fun getPendingSyncReports(): List<PotholeReport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: PotholeReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<PotholeReport>)

    @Update
    suspend fun update(report: PotholeReport)

    @Query("UPDATE pothole_reports SET upvotesCount = upvotesCount + 1, isUpvotedByMe = 1 WHERE id = :id")
    suspend fun upvoteReport(id: String)

    @Query("UPDATE pothole_reports SET isSyncPending = 0 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM pothole_reports WHERE id = :id")
    suspend fun delete(id: String)
}
