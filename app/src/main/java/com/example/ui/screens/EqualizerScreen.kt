package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerPreset
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.FrequencyVisualizerCanvas
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientHorizontalSlider
import com.example.ui.components.VerticalBandSlider
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CardDarkNavy
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EqualizerScreen(
    viewModel: AirPodsViewModel,
    onBackClick: () -> Unit
) {
    val isManualEnabled by viewModel.isManualEqEnabled.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val bandGains by viewModel.bandGains.collectAsState()
    val bassBoost by viewModel.bassBoost.collectAsState()
    val is3dAudioEnabled by viewModel.is3dAudioEnabled.collectAsState()

    val verticalScroll = rememberScrollState()
    val presetsScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(bottom = 90.dp)
    ) {
        AppHeader(
            title = "اکولایزر",
            onBackClick = onBackClick
        )

        // Frequency Visualizer Preview Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FrequencyVisualizerCanvas(
                    height = 90.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = selectedPreset.labelFa,
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Presets Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(presetsScroll)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val presets = listOf(
                EqualizerPreset.DEFAULT,
                EqualizerPreset.DEEP_BASS,
                EqualizerPreset.POP,
                EqualizerPreset.ROCK
            )

            presets.forEach { preset ->
                val isSelected = selectedPreset == preset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(BlueElectric, PurpleAccent))
                            } else {
                                Brush.horizontalGradient(listOf(CardDarkNavy, CardDarkNavy))
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyanAccent else CardBorderNavy,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.selectPreset(preset) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.labelFa,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manual Equalizer Toggle Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 18.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "اکولایزر دستی",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Switch(
                    checked = isManualEnabled,
                    onCheckedChange = { viewModel.toggleManualEq(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CyanAccent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF16253B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 10-Band Sliders Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 10.dp)
            ) {
                // dB indicators row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("+6 dB", color = TextMuted, fontSize = 9.sp)
                    Text("0 dB", color = TextMuted, fontSize = 9.sp)
                    Text("-6 dB", color = TextMuted, fontSize = 9.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The 10 vertical sliders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    viewModel.defaultBandFrequencies.forEachIndexed { index, freq ->
                        val gain = bandGains.getOrElse(index) { 0f }
                        VerticalBandSlider(
                            frequencyLabel = freq,
                            gainDb = gain,
                            onGainChanged = { newGain ->
                                if (isManualEnabled) {
                                    viewModel.setBandGain(index, newGain)
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bass Boost Slider Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تقویت باس",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "$bassBoost%",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )

                    GradientHorizontalSlider(
                        value = bassBoost / 100f,
                        onValueChange = { viewModel.setBassBoost((it * 100).toInt()) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3D Audio Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 18.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            imageVector = Icons.Default.SurroundSound,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "3D Audio",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "صدای سه‌بعدی و فضایی",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = is3dAudioEnabled,
                    onCheckedChange = { viewModel.toggle3dAudio(it) },
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
}
