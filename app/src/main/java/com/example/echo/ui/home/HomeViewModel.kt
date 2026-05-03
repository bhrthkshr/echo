package com.example.echo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echo.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications = repository.allNotifications

    private val _syncStatus = MutableStateFlow(SyncStatus.Active)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    init {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Syncing
            _syncStatus.value = if (repository.syncNotifications()) {
                SyncStatus.Active
            } else {
                SyncStatus.Paused
            }
        }
    }
}
