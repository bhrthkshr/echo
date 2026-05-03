package com.example.echo.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.echo.data.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EchoFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: NotificationRepository

    override fun onMessageReceived(message: RemoteMessage) {
        // Handle incoming sync notification
        // Parse message.data and store in repository
    }

    override fun onNewToken(token: String) {
        // Update FCM token on backend
    }
}
