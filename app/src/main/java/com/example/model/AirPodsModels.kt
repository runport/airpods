package com.example.model

enum class ConnectionStatus {
    CONNECTED,
    CONNECTING,
    DISCONNECTED,
    SCANNING,
    PERMISSION_REQUIRED
}

enum class SoundModeType {
    NORMAL,
    TRANSPARENCY,
    NOISE_CANCELLATION
}

enum class EqualizerPreset(val labelFa: String, val gains: List<Float>) {
    DEFAULT("پیش‌فرض", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    DEEP_BASS("باس عمیق", listOf(5f, 4f, 3f, 1f, 0f, 0f, -1f, 0f, 2f, 3f)),
    POP("پاپ", listOf(-1f, 1f, 3f, 4f, 3f, 1f, -1f, -1f, 1f, 2f)),
    ROCK("راک", listOf(4f, 3f, 1f, -1f, -2f, 0f, 2f, 3f, 4f, 5f)),
    CUSTOM("سفارشی", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
}

data class AirPodsDeviceInfo(
    val name: String = "AirPods Pro",
    val address: String = "AA:BB:CC:DD:EE:FF",
    val isConnected: Boolean = true,
    val rssi: Int = -58,
    val modelDescription: String = "Apple AirPods Pro (2nd Gen)",
    val firmwareVersion: String = "5.0.0"
)

data class BatteryStatus(
    val leftPercent: Int? = 100,
    val rightPercent: Int? = 100,
    val casePercent: Int? = 72,
    val isCharging: Boolean = true,
    val estimatedChargeTimeMinutes: Int = 45,
    val lastUpdated: String = "هم‌اکنون"
)

data class MusicTrack(
    val title: String = "بدون عنوان",
    val artist: String = "Artiste",
    val isPlaying: Boolean = false
)

enum class ScreenDestination {
    HOME,
    EQUALIZER,
    SETTINGS,
    BATTERY_DETAIL,
    SOUND_CONTROL,
    SOUND_MODES,
    FIND_AIRPODS,
    ADVANCED_SETTINGS
}
