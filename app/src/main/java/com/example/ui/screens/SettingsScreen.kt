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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: AirPodsViewModel,
    onNavigate: (ScreenDestination) -> Unit,
    onBackClick: () -> Unit
) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        AppHeader(
            title = "تنظیمات",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsNavigationRow(
                title = "نام دستگاه",
                subtitle = deviceInfo.name,
                icon = Icons.Default.Headset,
                onClick = {}
            )

            SettingsNavigationRow(
                title = "مدیریت اتصال",
                subtitle = "اتصال خودکار، چند دستگاه",
                icon = Icons.Default.Link,
                onClick = { onNavigate(ScreenDestination.FIND_AIRPODS) }
            )

            SettingsNavigationRow(
                title = "به‌روزرسانی فریم‌ور",
                subtitle = "آخرین نسخه: ${deviceInfo.firmwareVersion}",
                icon = Icons.Default.CloudDownload,
                onClick = {}
            )

            SettingsNavigationRow(
                title = "تنظیمات پیشرفته",
                subtitle = "تشخیص داخل گوش، میکروفون، صدا",
                icon = Icons.Default.Settings,
                onClick = { onNavigate(ScreenDestination.ADVANCED_SETTINGS) }
            )

            SettingsNavigationRow(
                title = "اطلاع‌رسانی‌ها",
                subtitle = "وضعیت باتری، هشدارها",
                icon = Icons.Default.Notifications,
                onClick = { showNotificationDialog = true }
            )

            SettingsNavigationRow(
                title = "درباره برنامه",
                subtitle = "نسخه 1.0.0",
                icon = Icons.Default.Info,
                onClick = { showAboutDialog = true }
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text("درباره AirPods Control", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "برنامه AirPods Control طراحی‌شده برای کنترل واقعی و پیشرفته ایرپاد روی دستگاه‌های اندرویدی (با سازگاری API 22+).\n\n" +
                            "شامل اکولایزر واقعی سیستم، دریافت باتری BLE، کنترل صدای رسانه و رابط کاربری شیشه‌ای تیره.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("متوجه شدم", color = CyanAccent)
                }
            }
        )
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Text("اطلاع‌رسانی‌ها", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "هشدارهای سطح باتری کم و اعلان پاپ‌آپ هنگام اتصال ایرپاد به صورت پیش‌فرض فعال است.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("بستن", color = CyanAccent)
                }
            }
        )
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueElectric.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
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

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
