package com.example.echo.ui.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.echo.ui.components.AppGlyph
import com.example.echo.ui.components.MainTab
import com.example.echo.ui.components.EchoScaffold
import com.example.echo.ui.components.StatusDot
import com.example.echo.ui.components.TonalCard
import com.example.echo.ui.theme.OnSurfaceVariant
import com.example.echo.ui.theme.OutlineVariant
import com.example.echo.ui.theme.Primary
import com.example.echo.ui.theme.PrimaryFixed
import com.example.echo.ui.theme.Secondary
import com.example.echo.ui.theme.Success
import com.example.echo.ui.theme.Surface
import com.example.echo.ui.theme.SurfaceContainer

private data class DeviceUi(val name: String, val detail: String, val type: String, val online: Boolean)

@Composable
fun DevicesScreen(onTabSelected: (MainTab) -> Unit) {
    val devices = listOf(
        DeviceUi("This Android Phone", "Last synced: Just now", "P", true),
        DeviceUi("MacBook Air M2", "Last synced: 4 hours ago", "L", false),
        DeviceUi("Studio Tablet", "Last synced: 12 minutes ago", "T", true),
        DeviceUi("Office PC", "Last synced: 2 days ago", "D", false)
    )

    EchoScaffold(
        selectedTab = MainTab.Devices,
        onTabSelected = onTabSelected
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Devices", style = MaterialTheme.typography.headlineLarge)
                        Text("Manage connected devices and sync status.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                    Button(onClick = { }) {
                        Text("Add")
                    }
                }
            }
            items(devices, key = { it.name }) { device ->
                DeviceCard(device)
            }
            item {
                AddDeviceCard()
            }
            item {
                TonalCard {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sync multiple devices effortlessly", style = MaterialTheme.typography.titleLarge, color = Primary)
                        Text(
                            "Connect your phone, laptop, tablet, and desktop to keep notifications in one calm feed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(device: DeviceUi) {
    TonalCard {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AppGlyph(device.type, color = if (device.online) Primary else Secondary, container = if (device.online) PrimaryFixed else SurfaceContainer)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusDot(if (device.online) Success else Secondary)
                    Text(
                        if (device.online) "ONLINE" else "OFFLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (device.online) Success else Secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column {
                Text(device.name, style = MaterialTheme.typography.titleLarge)
                Text(device.detail, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) {
                    Text("Rename")
                }
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) {
                    Text("Disconnect")
                }
            }
        }
    }
}

@Composable
private fun AddDeviceCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppGlyph("A")
        Text("Add Device", style = MaterialTheme.typography.titleLarge)
        Text("Connect a new phone or PC", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
    }
}
