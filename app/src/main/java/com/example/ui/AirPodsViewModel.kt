package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioControlManager
import com.example.bluetooth.BluetoothAirPodsManager
import com.example.data.AirPodsPreferences
import com.example.model.AirPodsDeviceInfo
import com.example.model.BatteryStatus
import com.example.model.ConnectionStatus
import com.example.model.EqualizerPreset
import com.example.model.MusicTrack
import com.example.model.ScreenDestination
import com.example.model.SoundModeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AirPodsViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = AirPodsPreferences(application)
    val audioManager = AudioControlManager(application)
    val bluetoothManager = BluetoothAirPodsManager(application)

    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<ScreenDestination>()

    val connectionStatus: StateFlow<ConnectionStatus> = bluetoothManager.connectionStatus
    val deviceInfo: StateFlow<AirPodsDeviceInfo> = bluetoothManager.deviceInfo
    val batteryStatus: StateFlow<BatteryStatus> = bluetoothManager.batteryStatus
    val discoveredDevices: StateFlow<List<AirPodsDeviceInfo>> = bluetoothManager.discoveredDevices
    val isScanning: StateFlow<Boolean> = bluetoothManager.isScanning

    private val _mediaVolume = MutableStateFlow(audioManager.getMediaVolume())
    val mediaVolume: StateFlow<Float> = _mediaVolume.asStateFlow()

    private val _nowPlayingTrack = MutableStateFlow(
        MusicTrack(
            title = "بدون عنوان",
            artist = "Artiste",
            isPlaying = false
        )
    )
    val nowPlayingTrack: StateFlow<MusicTrack> = _nowPlayingTrack.asStateFlow()

    private val _isManualEqEnabled = MutableStateFlow(prefs.isManualEqEnabled)
    val isManualEqEnabled: StateFlow<Boolean> = _isManualEqEnabled.asStateFlow()

    private val _selectedPreset = MutableStateFlow(
        try {
            EqualizerPreset.valueOf(prefs.selectedPreset)
        } catch (e: Exception) {
            EqualizerPreset.DEFAULT
        }
    )
    val selectedPreset: StateFlow<EqualizerPreset> = _selectedPreset.asStateFlow()

    val defaultBandFrequencies = listOf("31", "62", "125", "250", "500", "1K", "2K", "4K", "8K", "16K")

    private val _bandGains = MutableStateFlow<List<Float>>(
        List(10) { index ->
            prefs.getBandGain(index, 0f)
        }
    )
    val bandGains: StateFlow<List<Float>> = _bandGains.asStateFlow()

    private val _bassBoost = MutableStateFlow(prefs.bassBoostPercent)
    val bassBoost: StateFlow<Int> = _bassBoost.asStateFlow()

    private val _is3dAudioEnabled = MutableStateFlow(prefs.is3dAudioEnabled)
    val is3dAudioEnabled: StateFlow<Boolean> = _is3dAudioEnabled.asStateFlow()

    private val _soundMode = MutableStateFlow(
        try {
            SoundModeType.valueOf(prefs.soundMode)
        } catch (e: Exception) {
            SoundModeType.NORMAL
        }
    )
    val soundMode: StateFlow<SoundModeType> = _soundMode.asStateFlow()

    private val _inEarDetection = MutableStateFlow(prefs.inEarDetection)
    val inEarDetection: StateFlow<Boolean> = _inEarDetection.asStateFlow()

    private val _micOptimization = MutableStateFlow(prefs.microphoneOptimization)
    val micOptimization: StateFlow<Boolean> = _micOptimization.asStateFlow()

    private val _lowLatencyMode = MutableStateFlow(prefs.lowLatencyMode)
    val lowLatencyMode: StateFlow<Boolean> = _lowLatencyMode.asStateFlow()

    private val _floatingShortcut = MutableStateFlow(prefs.floatingShortcut)
    val floatingShortcut: StateFlow<Boolean> = _floatingShortcut.asStateFlow()

    private val _isLocatingSoundActive = MutableStateFlow(false)
    val isLocatingSoundActive: StateFlow<Boolean> = _isLocatingSoundActive.asStateFlow()

    init {
        audioManager.setBassBoost(_bassBoost.value)
        applyCurrentGainsToHardware()
    }

    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        }
        if (_currentScreen.value != ScreenDestination.HOME) {
            _currentScreen.value = ScreenDestination.HOME
            return true
        }
        return false
    }

    fun setMediaVolume(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        _mediaVolume.value = clamped
        audioManager.setMediaVolume(clamped)
    }

    fun togglePlayPause() {
        val current = _nowPlayingTrack.value
        _nowPlayingTrack.value = current.copy(isPlaying = !current.isPlaying)
    }

    fun toggleManualEq(enabled: Boolean) {
        _isManualEqEnabled.value = enabled
        prefs.isManualEqEnabled = enabled
        audioManager.setEqualizerEnabled(enabled)
    }

    fun selectPreset(preset: EqualizerPreset) {
        _selectedPreset.value = preset
        prefs.selectedPreset = preset.name
        if (preset != EqualizerPreset.CUSTOM) {
            val gains = preset.gains
            _bandGains.value = gains
            gains.forEachIndexed { index, gain ->
                prefs.setBandGain(index, gain)
                audioManager.setBandGain(index, gain)
            }
        }
    }

    fun setBandGain(index: Int, gainDb: Float) {
        val current = _bandGains.value.toMutableList()
        if (index in current.indices) {
            current[index] = gainDb
            _bandGains.value = current
            _selectedPreset.value = EqualizerPreset.CUSTOM
            prefs.selectedPreset = EqualizerPreset.CUSTOM.name
            prefs.setBandGain(index, gainDb)
            audioManager.setBandGain(index, gainDb)
        }
    }

    private fun applyCurrentGainsToHardware() {
        _bandGains.value.forEachIndexed { index, gain ->
            audioManager.setBandGain(index, gain)
        }
    }

    fun setBassBoost(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        _bassBoost.value = clamped
        prefs.bassBoostPercent = clamped
        audioManager.setBassBoost(clamped)
    }

    fun toggle3dAudio(enabled: Boolean) {
        _is3dAudioEnabled.value = enabled
        prefs.is3dAudioEnabled = enabled
    }

    fun selectSoundMode(mode: SoundModeType) {
        _soundMode.value = mode
        prefs.soundMode = mode.name
    }

    fun toggleInEarDetection(enabled: Boolean) {
        _inEarDetection.value = enabled
        prefs.inEarDetection = enabled
    }

    fun toggleMicOptimization(enabled: Boolean) {
        _micOptimization.value = enabled
        prefs.microphoneOptimization = enabled
    }

    fun toggleLowLatencyMode(enabled: Boolean) {
        _lowLatencyMode.value = enabled
        prefs.lowLatencyMode = enabled
    }

    fun toggleFloatingShortcut(enabled: Boolean) {
        _floatingShortcut.value = enabled
        prefs.floatingShortcut = enabled
    }

    fun toggleLocateSound() {
        if (_isLocatingSoundActive.value) {
            audioManager.stopLocateChirp()
            _isLocatingSoundActive.value = false
        } else {
            audioManager.playLocateChirp()
            _isLocatingSoundActive.value = true
        }
    }

    fun startBluetoothScan() {
        bluetoothManager.startScan()
    }

    fun stopBluetoothScan() {
        bluetoothManager.stopScan()
    }

    fun resetAllSettings() {
        prefs.resetAll()
        _isManualEqEnabled.value = true
        _selectedPreset.value = EqualizerPreset.DEFAULT
        _bandGains.value = EqualizerPreset.DEFAULT.gains
        _bassBoost.value = 45
        _is3dAudioEnabled.value = false
        _soundMode.value = SoundModeType.NORMAL
        _inEarDetection.value = true
        _micOptimization.value = true
        _lowLatencyMode.value = false
        _floatingShortcut.value = true
        audioManager.setBassBoost(45)
        applyCurrentGainsToHardware()
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
        bluetoothManager.release()
    }
}
