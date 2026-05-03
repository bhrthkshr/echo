package com.example.echo.data.local

import androidx.room.*
import com.example.echo.data.model.NotificationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    @Query("DELETE FROM notifications WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredNotifications(currentTime: Long)
}
