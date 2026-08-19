package com.example.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import java.util.UUID

object NotificationHelper {
    const val CHANNEL_ID_REPAIRS = "channel_road_repairs"
    const val CHANNEL_ID_SENSOR = "channel_sensor_detections"

    fun showPushNotification(
        context: Context,
        title: String,
        message: String,
        reportId: String = "",
        type: NotificationType = NotificationType.STATUS_UPDATE
    ) {
        val channelId = if (type == NotificationType.SENSOR_BUMP_DETECTED) {
            CHANNEL_ID_SENSOR
        } else {
            CHANNEL_ID_REPAIRS
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_REPORT_ID", reportId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            UUID.randomUUID().hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            try {
                notificationManager.notify(notificationId, builder.build())
            } catch (_: Exception) {
                // Ignore security or status exceptions in sandboxed preview
            }
        }
    }

    fun createNotificationObject(
        title: String,
        message: String,
        reportId: String,
        type: NotificationType
    ): AppNotification {
        return AppNotification(
            id = "NOTIF-" + UUID.randomUUID().toString().take(8).uppercase(),
            reportId = reportId,
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
    }
}
