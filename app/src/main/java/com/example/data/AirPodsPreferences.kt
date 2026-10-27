package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.EqualizerPreset
import com.example.model.SoundModeType

class AirPodsPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("airpods_prefs", Context.MODE_PRIVATE)

    var selectedPreset: String
        get() = prefs.getString("eq_preset", EqualizerPreset.DEFAULT.name) ?: EqualizerPreset.DEFAULT.name
        set(value) = prefs.edit().putString("eq_preset", value).apply()

    var isManualEqEnabled: Boolean
        get() = prefs.getBoolean("manual_eq_enabled", true)
        set(value) = prefs.edit().putBoolean("manual_eq_enabled", value).apply()

    var bassBoostPercent: Int
        get() = prefs.getInt("bass_boost", 45)
        set(value) = prefs.edit().putInt("bass_boost", value).apply()

    var is3dAudioEnabled: Boolean
        get() = prefs.getBoolean("3d_audio", false)
        set(value) = prefs.edit().putBoolean("3d_audio", value).apply()

    var soundMode: String
        get() = prefs.getString("sound_mode", SoundModeType.NORMAL.name) ?: SoundModeType.NORMAL.name
        set(value) = prefs.edit().putString("sound_mode", value).apply()

    var inEarDetection: Boolean
        get() = prefs.getBoolean("in_ear_detection", true)
        set(value) = prefs.edit().putBoolean("in_ear_detection", value).apply()

    var microphoneOptimization: Boolean
        get() = prefs.getBoolean("mic_optimization", true)
        set(value) = prefs.edit().putBoolean("mic_optimization", value).apply()

    var lowLatencyMode: Boolean
        get() = prefs.getBoolean("low_latency", false)
        set(value) = prefs.edit().putBoolean("low_latency", value).apply()

    var floatingShortcut: Boolean
        get() = prefs.getBoolean("floating_shortcut", true)
        set(value) = prefs.edit().putBoolean("floating_shortcut", value).apply()

    fun getBandGain(index: Int, defaultGain: Float): Float {
        return prefs.getFloat("band_gain_$index", defaultGain)
    }

    fun setBandGain(index: Int, gain: Float) {
        prefs.edit().putFloat("band_gain_$index", gain).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }
}
