package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.data.model.PotholeSeverity
import com.example.data.model.SensorDetectedPothole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.math.abs
import kotlin.math.sqrt

data class SensorTelemetry(
    val isMonitoring: Boolean = false,
    val currentGForce: Float = 1.0f,
    val peakGForce: Float = 1.0f,
    val gyroTiltX: Float = 0.0f,
    val gyroTiltY: Float = 0.0f,
    val gyroTiltZ: Float = 0.0f,
    val tripPotholeCount: Int = 0,
    val lastShockTime: Long = 0L,
    val recentDetections: List<SensorDetectedPothole> = emptyList()
)

class PotholeSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _telemetry = MutableStateFlow(SensorTelemetry())
    val telemetry: StateFlow<SensorTelemetry> = _telemetry.asStateFlow()

    private var lastDetectionTimestamp = 0L
    private val detectionCooldownMs = 1500L // Prevent duplicate spikes for single bump
    private val shockThresholdG = 2.1f // G-force spike threshold for road craters

    var onPotholeDetectedListener: ((SensorDetectedPothole) -> Unit)? = null

    fun startMonitoring() {
        _telemetry.value = _telemetry.value.copy(isMonitoring = true)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopMonitoring() {
        _telemetry.value = _telemetry.value.copy(isMonitoring = false)
        sensorManager.unregisterListener(this)
    }

    fun simulateBump(intensityG: Float = 2.7f, approxLocation: String = "Outer Ring Road (Near Flyover)") {
        val now = System.currentTimeMillis()
        val severity = when {
            intensityG > 2.8f -> PotholeSeverity.CRITICAL_HAZARD
            intensityG > 2.2f -> PotholeSeverity.SEVERE
            intensityG > 1.6f -> PotholeSeverity.MODERATE
            else -> PotholeSeverity.MINOR
        }

        val detection = SensorDetectedPothole(
            id = "SENSOR-" + UUID.randomUUID().toString().take(6).uppercase(),
            timestamp = now,
            latitude = 12.9279 + ((now % 100).toDouble() / 10000.0),
            longitude = 77.6271 + ((now % 100).toDouble() / 10000.0),
            gForceSpike = intensityG,
            gyroTilt = 2.4f,
            estimatedSeverity = severity,
            addressApprox = approxLocation,
            isConfirmed = false
        )

        val updatedList = listOf(detection) + _telemetry.value.recentDetections
        _telemetry.value = _telemetry.value.copy(
            currentGForce = intensityG,
            peakGForce = maxOf(_telemetry.value.peakGForce, intensityG),
            lastShockTime = now,
            tripPotholeCount = _telemetry.value.tripPotholeCount + 1,
            recentDetections = updatedList
        )

        onPotholeDetectedListener?.invoke(detection)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !_telemetry.value.isMonitoring) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]
                val totalAccel = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()
                val gForce = totalAccel / SensorManager.GRAVITY_EARTH

                val currentPeak = maxOf(_telemetry.value.peakGForce, gForce)
                _telemetry.value = _telemetry.value.copy(
                    currentGForce = gForce,
                    peakGForce = currentPeak
                )

                val now = System.currentTimeMillis()
                if (gForce >= shockThresholdG && (now - lastDetectionTimestamp > detectionCooldownMs)) {
                    lastDetectionTimestamp = now
                    triggerAutoPotholeDetection(gForce, _telemetry.value.gyroTiltZ)
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val gx = event.values[0]
                val gy = event.values[1]
                val gz = event.values[2]
                _telemetry.value = _telemetry.value.copy(
                    gyroTiltX = gx,
                    gyroTiltY = gy,
                    gyroTiltZ = gz
                )
            }
        }
    }

    private fun triggerAutoPotholeDetection(gForce: Float, gyroZ: Float) {
        val now = System.currentTimeMillis()
        val severity = when {
            gForce > 2.8f || abs(gyroZ) > 3.0f -> PotholeSeverity.CRITICAL_HAZARD
            gForce > 2.2f -> PotholeSeverity.SEVERE
            else -> PotholeSeverity.MODERATE
        }

        val detection = SensorDetectedPothole(
            id = "SENSOR-" + UUID.randomUUID().toString().take(6).uppercase(),
            timestamp = now,
            latitude = 12.9352 + ((now % 50).toDouble() / 10000.0),
            longitude = 77.6245 + ((now % 50).toDouble() / 10000.0),
            gForceSpike = gForce,
            gyroTilt = gyroZ,
            estimatedSeverity = severity,
            addressApprox = "Auto GPS Coordinate Detected (${String.format("%.4f", 12.9352)}, ${String.format("%.4f", 77.6245)})",
            isConfirmed = false
        )

        val updatedList = listOf(detection) + _telemetry.value.recentDetections
        _telemetry.value = _telemetry.value.copy(
            lastShockTime = now,
            tripPotholeCount = _telemetry.value.tripPotholeCount + 1,
            recentDetections = updatedList
        )

        onPotholeDetectedListener?.invoke(detection)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
