package com.example.echo.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.echo.data.model.NotificationItem
import com.example.echo.data.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EchoNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var repository: NotificationRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val notification = it.notification
            val extras = notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""
            val appName = runCatching {
                val appInfo = packageManager.getApplicationInfo(it.packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            }.getOrDefault(it.packageName)
            
            val item = NotificationItem(
                id = it.key,
                userId = "current_user_id", // Should be from Auth
                sourceDeviceId = "this_device_id",
                appPackage = it.packageName,
                appName = appName,
                title = title,
                message = text,
                timestamp = it.postTime,
                createdAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000),
                hash = "${it.packageName}$title$text".hashCode().toString()
            )
            
            serviceScope.launch {
                repository.sendNotification(item)
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
