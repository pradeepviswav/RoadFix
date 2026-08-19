package com.example.util

import com.example.data.model.PotholeReport
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ProximityClustering {

    /**
     * Calculates distance between two GPS coordinates in meters using the Haversine formula.
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusMeters = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusMeters * c
    }

    data class DuplicateMatch(
        val existingReport: PotholeReport,
        val distanceMeters: Double
    )

    /**
     * Checks if a new location has existing reported potholes within threshold meters (e.g. 150m).
     */
    fun findNearbyDuplicates(
        targetLat: Double,
        targetLon: Double,
        existingReports: List<PotholeReport>,
        thresholdMeters: Double = 150.0
    ): List<DuplicateMatch> {
        return existingReports.mapNotNull { report ->
            val dist = calculateDistanceMeters(targetLat, targetLon, report.latitude, report.longitude)
            if (dist <= thresholdMeters) {
                DuplicateMatch(report, dist)
            } else null
        }.sortedBy { it.distanceMeters }
    }

    /**
     * Clusters nearby potholes within 200m for radar/map visualization.
     */
    data class PotholeCluster(
        val centerLat: Double,
        val centerLon: Double,
        val reports: List<PotholeReport>,
        val radiusMeters: Double
    )

    fun clusterReports(reports: List<PotholeReport>, clusterRadiusMeters: Double = 200.0): List<PotholeCluster> {
        val visited = mutableSetOf<String>()
        val clusters = mutableListOf<PotholeCluster>()

        for (report in reports) {
            if (report.id in visited) continue
            val nearby = reports.filter { other ->
                other.id !in visited && calculateDistanceMeters(
                    report.latitude, report.longitude,
                    other.latitude, other.longitude
                ) <= clusterRadiusMeters
            }
            nearby.forEach { visited.add(it.id) }

            val avgLat = nearby.map { it.latitude }.average()
            val avgLon = nearby.map { it.longitude }.average()
            clusters.add(
                PotholeCluster(
                    centerLat = avgLat,
                    centerLon = avgLon,
                    reports = nearby,
                    radiusMeters = clusterRadiusMeters
                )
            )
        }
        return clusters
    }
}
