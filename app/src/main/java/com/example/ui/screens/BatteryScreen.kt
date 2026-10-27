package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Cases
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.GlassCard
import com.example.ui.components.RadialBatteryRing
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BatteryScreen(
    viewModel: AirPodsViewModel,
    onBackClick: () -> Unit
) {
    val batteryStatus by viewModel.batteryStatus.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        AppHeader(
            title = "وضعیت باتری",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large Center Battery Ring for Case
        BoxCenterCaseRing(
            casePercent = batteryStatus.casePercent
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dual Battery Rings for Left & Right Earbuds
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RadialBatteryRing(
                percentage = batteryStatus.leftPercent,
                label = "ایرپاد چپ",
                subLabel = "L",
                sizeDp = 105.dp,
                strokeWidthDp = 7.dp
            )

            RadialBatteryRing(
                percentage = batteryStatus.rightPercent,
                label = "ایرپاد راست",
                subLabel = "R",
                sizeDp = 105.dp,
                strokeWidthDp = 7.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Charging Status Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 18.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (batteryStatus.isCharging) "در حال شارژ" else "شارژ کامل / آماده استفاده",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "زمان تقریبی شارژ کامل: ${batteryStatus.estimatedChargeTimeMinutes} دقیقه",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Battery Care Tips Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نکات باتری",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• برای افزایش عمر باتری، از شارژر اصلی استفاده کنید.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• در زمان عدم استفاده، ایرپاد را در کیس نگه دارید.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• درصد باتری مستقیماً از داده‌های BLE واقعی هندزفری خوانده می‌شود.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun BoxCenterCaseRing(
    casePercent: Int?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RadialBatteryRing(
            percentage = casePercent,
            label = "",
            subLabel = "Case",
            sizeDp = 150.dp,
            strokeWidthDp = 9.dp,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Cases,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}
