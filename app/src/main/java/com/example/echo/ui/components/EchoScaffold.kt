package com.example.echo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.echo.ui.theme.OutlineVariant
import com.example.echo.ui.theme.Primary
import com.example.echo.ui.theme.PrimaryFixed
import com.example.echo.ui.theme.SurfaceContainerLowest

enum class MainTab(val route: String, val label: String, val icon: String) {
    Feed("feed", "Feed", "N"),
    Apps("apps", "Apps", "A"),
    Devices("devices", "Devices", "D"),
    Settings("settings", "Settings", "S")
}

@Composable
fun EchoScaffold(
    selectedTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            EchoTopBar(actions = actions)
        },
        bottomBar = {
            if (selectedTab != null) {
                NavigationBar(
                    containerColor = SurfaceContainerLowest,
                    tonalElevation = 0.dp
                ) {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { onTabSelected(tab) },
                            icon = { NavIcon(tab.icon, selectedTab == tab) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoTopBar(actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppGlyph(label = "E", color = Primary, container = PrimaryFixed)
                Text("Echo", fontWeight = FontWeight.SemiBold)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceContainerLowest,
            titleContentColor = Primary
        )
    )
}

@Composable
fun AppGlyph(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Primary,
    container: Color = PrimaryFixed
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavIcon(label: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryFixed else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Primary else Color.Unspecified, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TonalCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceContainerLowest,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
        content = content
    )
}

@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun HeaderAction(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label)
    }
}
