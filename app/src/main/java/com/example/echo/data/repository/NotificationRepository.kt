package com.example.echo.data.repository

import com.example.echo.data.local.NotificationDao
import com.example.echo.data.model.NotificationItem
import com.example.echo.data.remote.EchoApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val echoApi: EchoApi
) {
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()

    suspend fun syncNotifications(): Boolean {
        return try {
            val lastSync = 0L // TODO: Fetch from DataStore during API integration.
            val remoteNotifications = echoApi.getNotifications(lastSync)
            remoteNotifications.forEach {
                notificationDao.insertNotification(it)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendNotification(notification: NotificationItem) {
        notificationDao.insertNotification(notification)

        try {
            echoApi.sendNotification(notification)
        } catch (e: Exception) {
            // Keep the local copy. Retry/queueing will be added with API integration.
        }
    }
}
