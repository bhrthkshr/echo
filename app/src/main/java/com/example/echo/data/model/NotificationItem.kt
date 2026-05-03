package com.example.echo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val userId: String,
    val sourceDeviceId: String,
    val appPackage: String,
    val appName: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val createdAt: Long,
    val expiresAt: Long,
    val hash: String
)
