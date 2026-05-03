package com.example.echo.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.echo.ui.components.AppGlyph
import com.example.echo.ui.components.MainTab
import com.example.echo.ui.components.EchoScaffold
import com.example.echo.ui.components.TonalCard
import com.example.echo.ui.theme.OnSurfaceVariant
import com.example.echo.ui.theme.Primary
import com.example.echo.ui.theme.PrimaryFixed
import com.example.echo.ui.theme.Surface
import com.example.echo.ui.theme.SurfaceContainer

private data class FilterApp(val name: String, val detail: String, val enabled: Boolean)

@Composable
fun FilterScreen(onTabSelected: (MainTab) -> Unit) {
    val apps = remember {
        listOf(
            FilterApp("WhatsApp", "Last synced 2m ago", true),
            FilterApp("Slack", "Enterprise Workspace", true),
            FilterApp("Instagram", "Notifications silenced", false),
            FilterApp("Gmail", "Priority inbox only", true),
            FilterApp("LinkedIn", "Job alerts active", true),
            FilterApp("X", "Sync paused by user", false),
            FilterApp("Calendar", "Daily agenda sync", true)
        )
    }
    val selected = remember { mutableStateMapOf<String, Boolean>().also { map -> apps.forEach { map[it.name] = it.enabled } } }
    val query = remember { mutableStateOf("") }
    val visibleApps = apps.filter { it.name.contains(query.value, ignoreCase = true) }
    val selectedCount = selected.values.count { it }

    EchoScaffold(
        selectedTab = MainTab.Apps,
        onTabSelected = onTabSelected
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query.value,
                    onValueChange = { query.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search apps...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                MasterToggleCard(
                    checked = selected.values.all { it },
                    onCheckedChange = { checked -> apps.forEach { selected[it.name] = checked } }
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INSTALLED APPS", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                    Text("$selectedCount selected", style = MaterialTheme.typography.labelLarge, color = Primary)
                }
            }
            items(visibleApps, key = { it.name }) { app ->
                AppFilterRow(
                    name = app.name,
                    detail = app.detail,
                    enabled = selected[app.name] == true,
                    onEnabledChange = { selected[app.name] = it }
                )
            }
        }
    }
}

@Composable
private fun MasterToggleCard(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    TonalCard {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppGlyph("A", color = Primary, container = PrimaryFixed)
                Column {
                    Text("Sync all apps", style = MaterialTheme.typography.titleLarge)
                    Text("Enable sync for all installed applications", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun AppFilterRow(name: String, detail: String, enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) Color.Transparent else SurfaceContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppGlyph(label = name.take(1).uppercase(), color = if (enabled) Primary else OnSurfaceVariant)
            Column {
                Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                Text(detail, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
        }
        Switch(checked = enabled, onCheckedChange = onEnabledChange)
    }
}
