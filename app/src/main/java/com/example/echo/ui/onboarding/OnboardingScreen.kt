package com.example.echo.ui.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.echo.ui.components.AppGlyph
import com.example.echo.ui.components.EchoTopBar
import com.example.echo.ui.components.TonalCard
import com.example.echo.ui.theme.OnSurfaceVariant
import com.example.echo.ui.theme.Primary
import com.example.echo.ui.theme.PrimaryFixed
import com.example.echo.ui.theme.Secondary
import com.example.echo.ui.theme.SecondaryContainer
import com.example.echo.ui.theme.Surface
import com.example.echo.ui.theme.SurfaceContainer

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = { EchoTopBar() },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth()) {
                    Text("Get Started")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "By continuing, you agree to our Terms of Service and Privacy Policy.",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HeroPanel()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Welcome to Echo", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Keep your workflow uninterrupted. Sync notifications seamlessly across all your devices with reliable local-first behavior.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                Text("Setup Requirements", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            item {
                SetupStep(
                    label = "Notification Access",
                    step = "Step 1",
                    body = "Allow Echo to read incoming alerts so they can be securely synced to your other active devices.",
                    glyph = "N",
                    primary = true,
                    action = {
                        Button(onClick = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }) {
                            Text("Enable Access")
                        }
                    }
                )
            }
            item {
                SetupStep(
                    label = "Battery Optimization",
                    step = "Step 2",
                    body = "Exclude Echo from system sleep cycles to keep delivery responsive.",
                    glyph = "B",
                    primary = false,
                    action = {
                        OutlinedButton(onClick = { context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }) {
                            Text("Configure Settings")
                        }
                    }
                )
            }
            item {
                TonalCard {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppGlyph("P")
                        Text(
                            "Your privacy is the priority. During this stabilization phase, notifications are stored locally first.",
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
private fun HeroPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AppGlyph("P", modifier = Modifier.size(64.dp), color = Primary, container = PrimaryFixed)
            AppGlyph("L", modifier = Modifier.size(84.dp), color = Secondary, container = SecondaryContainer)
            AppGlyph("T", modifier = Modifier.size(56.dp), color = Primary, container = PrimaryFixed)
        }
    }
}

@Composable
private fun SetupStep(
    label: String,
    step: String,
    body: String,
    glyph: String,
    primary: Boolean,
    action: @Composable () -> Unit
) {
    TonalCard {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppGlyph(
                label = glyph,
                color = if (primary) MaterialTheme.colorScheme.onPrimary else Secondary,
                container = if (primary) Primary else SecondaryContainer
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(step, style = MaterialTheme.typography.labelSmall, color = Secondary)
                }
                Text(body, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                action()
            }
        }
    }
}
