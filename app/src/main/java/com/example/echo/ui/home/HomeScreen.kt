package com.example.echo.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.echo.data.model.NotificationItem
import com.example.echo.ui.components.AppGlyph
import com.example.echo.ui.components.HeaderAction
import com.example.echo.ui.components.MainTab
import com.example.echo.ui.components.EchoScaffold
import com.example.echo.ui.components.StatusDot
import com.example.echo.ui.components.TonalCard
import com.example.echo.ui.theme.Error
import com.example.echo.ui.theme.ErrorContainer
import com.example.echo.ui.theme.OnErrorContainer
import com.example.echo.ui.theme.OnSurfaceVariant
import com.example.echo.ui.theme.Primary
import com.example.echo.ui.theme.PrimaryFixed
import com.example.echo.ui.theme.Secondary
import com.example.echo.ui.theme.SecondaryContainer
import com.example.echo.ui.theme.Success
import com.example.echo.ui.theme.Surface
import com.example.echo.ui.theme.SurfaceContainer
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(
    onTabSelected: (MainTab) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    val syncStatus by viewModel.syncStatus.collectAsState()
    val context = LocalContext.current

    EchoScaffold(
        selectedTab = MainTab.Feed,
        onTabSelected = onTabSelected,
        actions = {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusDot(color = if (syncStatus == SyncStatus.Paused) Secondary else Success)
                Text(
                    text = if (syncStatus == SyncStatus.Paused) "Local only" else "Synced",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { },
                text = { Text("Sync") },
                icon = { Text("S", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FilterChips() }
            if (notifications.isEmpty()) {
                item {
                    SetupCard(
                        onEnableNotificationAccess = {
                            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        },
                        onOpenFilters = { onTabSelected(MainTab.Apps) },
                        onOpenDevices = { onTabSelected(MainTab.Devices) }
                    )
                }
                item { SampleFeedPreview() }
            } else {
                val grouped = notifications.groupBy { it.appName.ifBlank { it.appPackage } }
                grouped.forEach { (appName, appNotifications) ->
                    item {
                        AppGroupHeader(appName = appName, count = appNotifications.size)
                    }
                    items(appNotifications, key = { it.id }) { notification ->
                        NotificationCard(notification = notification)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChips() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip("All", selected = true)
        FilterChip("Gmail")
        FilterChip("Slack")
        FilterChip("Calendar")
        FilterChip("System")
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (selected) Primary else SurfaceContainer,
        contentColor = if (selected) Color.White else OnSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun AppGroupHeader(appName: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppGlyph(label = appName.take(1).uppercase(), modifier = Modifier.size(28.dp))
            Text(appName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        Text("$count new", style = MaterialTheme.typography.labelSmall, color = Secondary)
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    TonalCard {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppGlyph(label = notification.appName.take(1).ifBlank { "N" }.uppercase())
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = notification.title.ifBlank { notification.appName },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(notification.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.appPackage,
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    maxLines = 1
                )
                Text(
                    text = notification.message.ifBlank { "Notification content unavailable" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SetupCard(
    onEnableNotificationAccess: () -> Unit,
    onOpenFilters: () -> Unit,
    onOpenDevices: () -> Unit
) {
    TonalCard {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Welcome to Echo", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Enable notification access, choose which apps sync, and connect your devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
            Button(onClick = onEnableNotificationAccess, modifier = Modifier.fillMaxWidth()) {
                Text("Enable notification access")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenFilters, modifier = Modifier.weight(1f)) {
                    Text("Apps")
                }
                OutlinedButton(onClick = onOpenDevices, modifier = Modifier.weight(1f)) {
                    Text("Devices")
                }
            }
        }
    }
}

@Composable
private fun SampleFeedPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppGroupHeader("Slack", 2)
        PreviewNotification("Sarah Jenkins", "Project Sync", "Roadmap updates are ready for review.", "Just now")
        PreviewNotification("David Chen", "Engineering", "Deployment completed successfully.", "15m ago")
        AppGroupHeader("System", 1)
        PreviewNotification("MacBook Pro", "Low Battery Warning", "Remote device battery is at 10%.", "2h ago", error = true)
    }
}

@Composable
private fun PreviewNotification(title: String, subtitle: String, message: String, time: String, error: Boolean = false) {
    TonalCard {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (error) ErrorContainer else SecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(title.take(1), color = if (error) OnErrorContainer else Primary, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), maxLines = 1)
                    Text(time, style = MaterialTheme.typography.labelSmall, color = Secondary)
                }
                Text(subtitle, style = MaterialTheme.typography.labelLarge, color = if (error) Error else Primary)
                Text(message, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
            }
        }
    }
}

enum class SyncStatus {
    Active, Syncing, Paused, Error
}
