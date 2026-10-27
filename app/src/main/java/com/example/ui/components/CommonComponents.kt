package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CardDarkNavy
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardDarkNavy,
    borderColor: Color = CardBorderNavy,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = modifier
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun RadialBatteryRing(
    percentage: Int?,
    label: String,
    subLabel: String? = null,
    sizeDp: Dp = 120.dp,
    strokeWidthDp: Dp = 8.dp,
    ringColor: Color = CyanAccent,
    icon: @Composable (() -> Unit)? = null
) {
    val progress = (percentage?.toFloat() ?: 0f) / 100f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(sizeDp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(sizeDp)) {
                val strokePx = strokeWidthDp.toPx()
                val diameter = size.minDimension - strokePx
                val topLeft = Offset(strokePx / 2, strokePx / 2)
                val arcSize = Size(diameter, diameter)

                // Track
                drawArc(
                    color = Color(0xFF16253B),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Active Arc
                if (percentage != null && percentage > 0) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(CyanAccent, BlueElectric, CyanAccent)
                        ),
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = if (percentage != null) "$percentage%" else "نامشخص",
                    color = TextPrimary,
                    fontSize = if (sizeDp > 100.dp) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subLabel != null) {
                    Text(
                        text = subLabel,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FrequencyVisualizerCanvas(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val h = size.height
        val midY = h / 2f

        // Draw multiple smoothed frequency wave curves
        val path1 = Path()
        val path2 = Path()

        path1.moveTo(0f, midY)
        path2.moveTo(0f, midY)

        val steps = 80
        for (i in 0..steps) {
            val x = (i.toFloat() / steps) * width
            val normX = (i.toFloat() / steps)

            // Envelope to taper at ends
            val envelope = sin(normX * Math.PI).toFloat()

            val y1 = midY + sin(normX * 10f + phase) * (h * 0.35f) * envelope
            val y2 = midY + sin(normX * 14f - phase * 0.8f) * (h * 0.25f) * envelope

            if (i == 0) {
                path1.moveTo(x, y1)
                path2.moveTo(x, y2)
            } else {
                path1.lineTo(x, y1)
                path2.lineTo(x, y2)
            }
        }

        drawPath(
            path = path1,
            brush = Brush.horizontalGradient(
                listOf(PurpleNeon, CyanAccent, BlueElectric)
            ),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        drawPath(
            path = path2,
            brush = Brush.horizontalGradient(
                listOf(CyanAccent.copy(alpha = 0.5f), PurpleNeon.copy(alpha = 0.8f))
            ),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun VerticalBandSlider(
    frequencyLabel: String,
    gainDb: Float,
    onGainChanged: (Float) -> Unit,
    minGain: Float = -6f,
    maxGain: Float = 6f,
    modifier: Modifier = Modifier
) {
    var currentGain by remember(gainDb) { mutableFloatStateOf(gainDb) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(130.dp)
                .pointerInput(minGain, maxGain) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        val deltaPercent = -dragAmount / 130f
                        val deltaGain = deltaPercent * (maxGain - minGain)
                        val newGain = (currentGain + deltaGain).coerceIn(minGain, maxGain)
                        currentGain = newGain
                        onGainChanged(newGain)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Track
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF1E304B), RoundedCornerShape(2.dp))
            )

            // Thumb
            val normalized = ((currentGain - minGain) / (maxGain - minGain)).coerceIn(0f, 1f)
            val thumbOffset = ((1f - normalized) * 110f).dp

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = thumbOffset)
                        .size(16.dp)
                        .align(Alignment.TopCenter)
                        .shadow(4.dp, CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(CyanAccent, BlueElectric)
                            ),
                            shape = CircleShape
                        )
                        .border(1.5.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = frequencyLabel,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GradientHorizontalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = CyanAccent,
            activeTrackColor = CyanAccent,
            inactiveTrackColor = Color(0xFF1C2C45)
        )
    )
}

@Composable
fun AppHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = TextPrimary
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (trailingContent != null) {
            trailingContent()
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
