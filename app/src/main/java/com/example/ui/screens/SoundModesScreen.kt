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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SoundModeType
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CardDarkNavy
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SoundModesScreen(
    viewModel: AirPodsViewModel,
    onBackClick: () -> Unit
) {
    val currentMode by viewModel.soundMode.collectAsState()
    var isFilterActiveOnly by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        AppHeader(
            title = "حالت صدا",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Pill Filter (فعال / همه حالت‌ها)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardDarkNavy)
                .border(1.dp, CardBorderNavy, RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isFilterActiveOnly) {
                            Modifier.background(Brush.horizontalGradient(listOf(BlueElectric, PurpleAccent)))
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable { isFilterActiveOnly = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "فعال",
                    color = if (isFilterActiveOnly) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (!isFilterActiveOnly) {
                            Modifier.background(Brush.horizontalGradient(listOf(BlueElectric, PurpleAccent)))
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable { isFilterActiveOnly = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "همه حالت‌ها",
                    color = if (!isFilterActiveOnly) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sound Mode Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SoundModeSelectCard(
                title = "حالت عادی",
                subtitle = "صدای متعادل و طبیعی",
                icon = Icons.Default.Tune,
                isSelected = currentMode == SoundModeType.NORMAL,
                onClick = { viewModel.selectSoundMode(SoundModeType.NORMAL) }
            )

            SoundModeSelectCard(
                title = "حالت شفافیت",
                subtitle = "شنیدن صدای محیط اطراف",
                icon = Icons.Default.Hearing,
                isSelected = currentMode == SoundModeType.TRANSPARENCY,
                onClick = { viewModel.selectSoundMode(SoundModeType.TRANSPARENCY) }
            )

            SoundModeSelectCard(
                title = "حالت حذف نویز",
                subtitle = "کاهش نویز محیط",
                icon = Icons.Default.GraphicEq,
                isSelected = currentMode == SoundModeType.NOISE_CANCELLATION,
                onClick = { viewModel.selectSoundMode(SoundModeType.NOISE_CANCELLATION) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Environment Guidance Card
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
                        text = "حالت محیطی",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "با توجه به محیط خود، حالت مناسب را انتخاب کنید. در محیط‌های پر سر و صدا حالت حذف نویز و در خیابان حالت شفافیت پیشنهاد می‌شود.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SoundModeSelectCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isSelected) CyanAccent else CardBorderNavy,
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
                    .background(
                        if (isSelected) CyanAccent.copy(alpha = 0.2f) else Color(0xFF142033)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) CyanAccent else TextMuted,
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
                    fontSize = 12.sp
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(CyanAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "انتخاب شده",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
