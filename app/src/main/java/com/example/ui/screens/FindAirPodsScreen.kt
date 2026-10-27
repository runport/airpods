package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FindAirPodsScreen(
    viewModel: AirPodsViewModel,
    onBackClick: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val isChirpActive by viewModel.isLocatingSoundActive.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(
            title = "جستجوی ایرپاد",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Concentric Radar Visualizer
        RadarVisualizer(
            isScanning = isScanning,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "جستجوی ایرپاد",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "برای پیدا کردن ایرپادها، روی دکمه زیر کلیک کنید.",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 30.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Start/Stop Search Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    if (isScanning) {
                        Brush.horizontalGradient(listOf(DangerRed, Color(0xFFC62828)))
                    } else {
                        Brush.horizontalGradient(listOf(BlueElectric, PurpleAccent))
                    }
                )
                .clickable {
                    if (isScanning) viewModel.stopBluetoothScan() else viewModel.startBluetoothScan()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isScanning) "توقف جستجو" else "شروع جستجو",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Play Tone Locator Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CyanAccent.copy(alpha = 0.15f))
                .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .clickable { viewModel.toggleLocateSound() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isChirpActive) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isChirpActive) "توقف پخش صدا" else "پخش صدای موقعیت‌یاب",
                    color = CyanAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Last Location Card
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BlueElectric.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "آخرین موقعیت",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "خانه • ۵ دقیقه قبل",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "سیگنال RSSI: ${deviceInfo.rssi} dBm (تقریبی)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadarVisualizer(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2 - 8.dp.toPx()

            // Draw concentric radar circles
            for (i in 1..4) {
                val r = (maxRadius / 4) * i
                drawCircle(
                    color = Color(0xFF16253B),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Radar beam if scanning
            if (isScanning) {
                val rad = Math.toRadians(angle.toDouble())
                val beamEnd = Offset(
                    center.x + (maxRadius * cos(rad)).toFloat(),
                    center.y + (maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.radialGradient(
                        listOf(CyanAccent, Color.Transparent),
                        center = center,
                        radius = maxRadius
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.dp.toPx()
                )

                // Simulated blip dot
                val blipX = center.x + (maxRadius * 0.6f * cos(Math.toRadians(45.0))).toFloat()
                val blipY = center.y + (maxRadius * 0.6f * sin(Math.toRadians(45.0))).toFloat()
                drawCircle(
                    color = CyanAccent,
                    radius = 4.dp.toPx(),
                    center = Offset(blipX, blipY)
                )
            }
        }

        // Center Earbuds Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BlueElectric.copy(alpha = 0.25f))
                .border(2.dp, CyanAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Headset,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
