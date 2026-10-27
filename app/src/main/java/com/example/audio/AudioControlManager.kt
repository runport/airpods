package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.util.Log

class AudioControlManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var toneGenerator: ToneGenerator? = null

    var isHardwareEqualizerSupported: Boolean = false
        private set
    var numberOfBands: Int = 10
        private set

    init {
        initAudioEffects()
    }

    private fun initAudioEffects() {
        // Try session 0 first (global output)
        if (!tryAttachEqualizer(0)) {
            // Try standard media audio session IDs on Android 5.1
            for (session in listOf(1, 2, 3, 4, 5, 10, 100)) {
                if (tryAttachEqualizer(session)) break
            }
        }

        if (!isHardwareEqualizerSupported) {
            Log.d("AudioControlManager", "Software equalizer active with 10 bands")
            numberOfBands = 10
        }
    }

    private fun tryAttachEqualizer(sessionId: Int): Boolean {
        return try {
            val eq = Equalizer(0, sessionId)
            eq.enabled = true
            numberOfBands = eq.numberOfBands.toInt().coerceIn(3, 10)
            equalizer = eq
            isHardwareEqualizerSupported = true

            try {
                val bb = BassBoost(0, sessionId)
                bb.enabled = true
                bassBoost = bb
            } catch (e: Exception) {
                Log.w("AudioControlManager", "BassBoost unavailable on session $sessionId: ${e.message}")
            }

            Log.d("AudioControlManager", "Equalizer attached to session $sessionId with $numberOfBands bands")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getMediaVolume(): Float {
        val am = audioManager ?: return 0.7f
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return 0.7f
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    }

    fun setMediaVolume(volumeFraction: Float) {
        val am = audioManager ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return
        val targetIndex = (volumeFraction.coerceIn(0f, 1f) * max).toInt()
        try {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, targetIndex, 0)
        } catch (e: Exception) {
            Log.e("AudioControlManager", "Error setting stream volume: ${e.message}")
        }
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        val eq = equalizer ?: return
        try {
            val range = eq.bandLevelRange
            val minMb = range[0]
            val maxMb = range[1]
            val targetMb = (gainDb * 100).toInt().coerceIn(minMb.toInt(), maxMb.toInt()).toShort()
            if (bandIndex < eq.numberOfBands) {
                eq.setBandLevel(bandIndex.toShort(), targetMb)
            }
        } catch (e: Exception) {
            Log.w("AudioControlManager", "Failed to set band level: ${e.message}")
        }
    }

    fun setBassBoost(percent: Int) {
        val bb = bassBoost ?: return
        try {
            val strength = (percent.coerceIn(0, 100) * 10).toShort()
            bb.setStrength(strength)
        } catch (e: Exception) {
            Log.w("AudioControlManager", "Failed to set bass boost: ${e.message}")
        }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
        } catch (e: Exception) {
            Log.w("AudioControlManager", "Error toggling equalizer: ${e.message}")
        }
    }

    fun playLocateChirp() {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600)
        } catch (e: Exception) {
            Log.w("AudioControlManager", "ToneGenerator error: ${e.message}")
        }
    }

    fun stopLocateChirp() {
        try {
            toneGenerator?.stopTone()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun release() {
        try {
            equalizer?.release()
            equalizer = null
            bassBoost?.release()
            bassBoost = null
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.w("AudioControlManager", "Error releasing audio effects: ${e.message}")
        }
    }
}
