package com.example.echo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.echo.data.model.NotificationItem

@Database(entities = [NotificationItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
