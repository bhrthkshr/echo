package com.example.echo.data.remote

import com.example.echo.data.model.NotificationItem
import retrofit2.http.*

interface EchoApi {
    @POST("/notifications")
    suspend fun sendNotification(@Body notification: NotificationItem): ApiResponse<Unit>

    @GET("/notifications")
    suspend fun getNotifications(@Query("since") since: Long): List<NotificationItem>

    @POST("/devices/register")
    suspend fun registerDevice(@Body deviceRegistration: DeviceRegistration): ApiResponse<Unit>

    @GET("/devices")
    suspend fun getDevices(): List<DeviceItem>
}

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)

data class DeviceRegistration(
    val deviceId: String,
    val fcmToken: String,
    val name: String
)

data class DeviceItem(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val lastSeen: Long
)
