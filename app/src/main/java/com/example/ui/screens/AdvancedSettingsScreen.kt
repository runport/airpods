package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdvancedSettingsScreen(
    viewModel: AirPodsViewModel,
    onBackClick: () -> Unit
) {
    val inEar by viewModel.inEarDetection.collectAsState()
    val micOpt by viewModel.micOptimization.collectAsState()
    val lowLatency by viewModel.lowLatencyMode.collectAsState()
    val shortcut by viewModel.floatingShortcut.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showAmbientDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        AppHeader(
            title = "تنظیمات پیشرفته",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdvancedToggleRow(
                title = "تشخیص داخل گوش",
                subtitle = "پخش خودکار هنگام قرار دادن ایرپاد",
                icon = Icons.Default.Hearing,
                checked = inEar,
                onCheckedChange = { viewModel.toggleInEarDetection(it) }
            )

            AdvancedToggleRow(
                title = "میکروفون",
                subtitle = "بهینه‌سازی مکالمه و تمرکز صوتی",
                icon = Icons.Default.Mic,
                checked = micOpt,
                onCheckedChange = { viewModel.toggleMicOptimization(it) }
            )

            AdvancedToggleRow(
                title = "تأخیر کم (Low Latency)",
                subtitle = "مناسب برای بازی و ویدیو",
                icon = Icons.Default.Speed,
                checked = lowLatency,
                onCheckedChange = { viewModel.toggleLowLatencyMode(it) }
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showAmbientDialog = true },
                cornerRadius = 18.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BlueElectric.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تنظیمات صدای محیط",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "میزان شفافیت و حذف نویز",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AdvancedToggleRow(
                title = "ایجاد میانبر",
                subtitle = "دکمه شناور برای کنترل سریع",
                icon = Icons.Default.Widgets,
                checked = shortcut,
                onCheckedChange = { viewModel.toggleFloatingShortcut(it) }
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showResetDialog = true },
                cornerRadius = 18.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DangerRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تنظیم مجدد به حالت کارخانه",
                            color = DangerRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تمام تنظیمات بازنشانی می‌شوند",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("بازنشانی تنظیمات", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "آیا از بازنشانی تمام مقادیر اکولایزر، صدا و حالت‌های ذخیره شده اطمینان دارید؟",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("بله، بازنشانی شود", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }

    if (showAmbientDialog) {
        AlertDialog(
            onDismissRequest = { showAmbientDialog = false },
            title = {
                Text("تنظیمات صدای محیط", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "تنظیمات صدای محیط و شدت نویزگیر به صورت خودکار بر اساس سطح نویز میکروفون خارجی تطبیق داده می‌شود.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAmbientDialog = false }) {
                    Text("تأیید", color = CyanAccent)
                }
            }
        )
    }
}

@Composable
fun AdvancedToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BlueElectric.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CyanAccent,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = Color(0xFF16253B)
                )
            )
        }
    }
}
