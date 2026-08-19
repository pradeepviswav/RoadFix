package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.local.AppDatabase
import com.example.data.repository.PotholeRepository
import com.example.notification.NotificationHelper

class SadakRakshakApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: PotholeRepository by lazy { PotholeRepository(database, this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationHelper.CHANNEL_ID_REPAIRS,
                "Road Repair & Hazard Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Push notifications for pothole status updates, work progress, and road repairs."
                enableVibration(true)
                enableLights(true)
            }
            val sensorChannel = NotificationChannel(
                NotificationHelper.CHANNEL_ID_SENSOR,
                "Drive Sensor Auto-Detections",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when accelerometer/gyroscope sensors spot severe road bumps or potholes."
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(sensorChannel)
        }
    }

    companion object {
        lateinit var instance: SadakRakshakApp
            private set
    }
}
