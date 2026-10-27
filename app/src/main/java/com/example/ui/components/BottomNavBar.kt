package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SurfaceDarkNavy
import com.example.ui.theme.TextMuted

data class NavTabItem(
    val destination: ScreenDestination,
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

@Composable
fun AirPodsBottomNavBar(
    currentScreen: ScreenDestination,
    onTabSelected: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavTabItem(
            destination = ScreenDestination.HOME,
            title = "خانه",
            filledIcon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home
        ),
        NavTabItem(
            destination = ScreenDestination.EQUALIZER,
            title = "اکولایزر",
            filledIcon = Icons.Filled.Equalizer,
            outlinedIcon = Icons.Outlined.Equalizer
        ),
        NavTabItem(
            destination = ScreenDestination.SETTINGS,
            title = "تنظیمات",
            filledIcon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDarkNavy.copy(alpha = 0.95f))
            .border(1.dp, CardBorderNavy, RoundedCornerShape(28.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.destination) {
                    ScreenDestination.HOME -> currentScreen == ScreenDestination.HOME ||
                            currentScreen == ScreenDestination.BATTERY_DETAIL ||
                            currentScreen == ScreenDestination.SOUND_CONTROL ||
                            currentScreen == ScreenDestination.SOUND_MODES ||
                            currentScreen == ScreenDestination.FIND_AIRPODS
                    ScreenDestination.EQUALIZER -> currentScreen == ScreenDestination.EQUALIZER
                    ScreenDestination.SETTINGS -> currentScreen == ScreenDestination.SETTINGS ||
                            currentScreen == ScreenDestination.ADVANCED_SETTINGS
                    else -> false
                }

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) CyanAccent else TextMuted,
                    label = "iconColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(item.destination)
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .clip(CircleShape)
                                        .background(CyanAccent.copy(alpha = 0.15f))
                                } else Modifier
                            )
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                            contentDescription = item.title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.title,
                        color = iconColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
