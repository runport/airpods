package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.AirPodsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientHorizontalSlider
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CardDarkNavy
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: AirPodsViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val batteryStatus by viewModel.batteryStatus.collectAsState()
    val mediaVolume by viewModel.mediaVolume.collectAsState()
    val nowPlaying by viewModel.nowPlayingTrack.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onNavigate(ScreenDestination.SETTINGS) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "منو",
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AirPods Control",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (deviceInfo.isConnected) "Connected" else "Disconnected",
                    color = if (deviceInfo.isConnected) CyanAccent else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = { onNavigate(ScreenDestination.SETTINGS) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ========== دو ایرپاد جداگانه بدون کیس با حلقه سبز دور ایرپاد فعال ==========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left AirPod Unit
            AirPodUnit(
                label = "L",
                subLabel = "چپ",
                percent = batteryStatus.leftPercent,
                isActive = batteryStatus.isLeftActive,
                onClick = { onNavigate(ScreenDestination.BATTERY_DETAIL) }
            )

            // Right AirPod Unit
            AirPodUnit(
                label = "R",
                subLabel = "راست",
                percent = batteryStatus.rightPercent,
                isActive = batteryStatus.isRightActive,
                onClick = { onNavigate(ScreenDestination.BATTERY_DETAIL) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Case status زیر دو ایرپاد
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = { onNavigate(ScreenDestination.BATTERY_DETAIL) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.BatteryFull,
                    contentDescription = null,
                    tint = if (batteryStatus.casePercent != null) SuccessGreen else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "کیس شارژ: ",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = if (batteryStatus.casePercent != null)
                        "${batteryStatus.casePercent}%" else "—",
                    color = if (batteryStatus.isCharging) SuccessGreen else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (batteryStatus.isCharging) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⚡", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // نام مدل ایرپاد
        Text(
            text = deviceInfo.name.ifEmpty { "AirPods Pro" },
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Now Playing Media Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = { viewModel.togglePlayPause() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueElectric.copy(alpha = 0.2f))
                        .border(1.dp, BlueElectric.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "در حال پخش",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = nowPlaying.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = nowPlaying.artist,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.15f))
                        .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (nowPlaying.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "پخش / توقف",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Media Volume Slider Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = { onNavigate(ScreenDestination.SOUND_CONTROL) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = "کم کردن صدا",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                GradientHorizontalSlider(
                    value = mediaVolume,
                    onValueChange = { viewModel.setMediaVolume(it) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "زیاد کردن صدا",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Quick Action Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickShortcutCard(
                title = "اکولایزر",
                icon = Icons.Default.Equalizer,
                onClick = { onNavigate(ScreenDestination.EQUALIZER) },
                modifier = Modifier.weight(1f)
            )
            QuickShortcutCard(
                title = "حالت صدا",
                icon = Icons.Default.Tune,
                onClick = { onNavigate(ScreenDestination.SOUND_MODES) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickShortcutCard(
                title = "جستجوی ایرپاد",
                icon = Icons.Default.Radar,
                onClick = { onNavigate(ScreenDestination.FIND_AIRPODS) },
                modifier = Modifier.weight(1f)
            )
            QuickShortcutCard(
                title = "تنظیمات بیشتر",
                icon = Icons.Default.Settings,
                onClick = { onNavigate(ScreenDestination.SETTINGS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AirPodUnit(
    label: String,
    subLabel: String,
    percent: Int?,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(105.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A1424))
                .border(
                    width = if (isActive) 3.dp else 1.5.dp,
                    color = if (isActive) SuccessGreen else CardBorderNavy,
                    shape = CircleShape
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Headset,
                    contentDescription = "$label $subLabel",
                    tint = if (isActive) SuccessGreen else CyanAccent,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$label ($subLabel)",
                    color = if (isActive) SuccessGreen else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (percent != null) "$percent%" else "—",
            color = if (isActive) SuccessGreen else TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickShortcutCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(64.dp),
        onClick = onClick,
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
