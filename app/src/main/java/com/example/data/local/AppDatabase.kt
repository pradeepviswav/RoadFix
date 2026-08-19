package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppNotification
import com.example.data.model.PotholeReport
import com.example.data.model.SensorDetectedPothole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PotholeReport::class, SensorDetectedPothole::class, AppNotification::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun potholeDao(): PotholeDao
    abstract fun sensorDetectionDao(): SensorDetectionDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sadak_rakshak_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate seed data asynchronously
                        CoroutineScope(Dispatchers.IO).launch {
                            getDatabase(context).potholeDao().insertAll(InitialSeedData.seedReports)
                            InitialSeedData.seedNotifications.forEach {
                                getDatabase(context).notificationDao().insert(it)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
