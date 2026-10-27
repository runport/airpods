package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenDestination
import com.example.ui.AirPodsViewModel
import com.example.ui.components.AirPodsBottomNavBar
import com.example.ui.screens.AdvancedSettingsScreen
import com.example.ui.screens.BatteryScreen
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.FindAirPodsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SoundControlScreen
import com.example.ui.screens.SoundModesScreen
import com.example.ui.theme.BgDarkNavy
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AirPodsAppRoot()
                }
            }
        }
    }
}

@Composable
fun AirPodsAppRoot(
    viewModel: AirPodsViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Runtime permission launcher for Bluetooth / Location
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        permissionsLauncher.launch(permissions.toTypedArray())
    }

    // Handle system back navigation across sub-screens
    BackHandler(enabled = currentScreen != ScreenDestination.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarkNavy),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            AirPodsBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = { tab ->
                    viewModel.navigateTo(tab)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkNavy)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                ScreenDestination.EQUALIZER -> EqualizerScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) },
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.BATTERY_DETAIL -> BatteryScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.SOUND_CONTROL -> SoundControlScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.SOUND_MODES -> SoundModesScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.FIND_AIRPODS -> FindAirPodsScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
                ScreenDestination.ADVANCED_SETTINGS -> AdvancedSettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.navigateBack() }
                )
            }
        }
    }
}
