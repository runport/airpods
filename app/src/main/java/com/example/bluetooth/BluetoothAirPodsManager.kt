package com.example.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.model.AirPodsDeviceInfo
import com.example.model.BatteryStatus
import com.example.model.ConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothAirPodsManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.CONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _deviceInfo = MutableStateFlow(
        AirPodsDeviceInfo(
            name = "AirPods Pro",
            address = "AA:BB:CC:DD:EE:FF",
            isConnected = true,
            rssi = -56,
            modelDescription = "Apple AirPods Pro"
        )
    )
    val deviceInfo: StateFlow<AirPodsDeviceInfo> = _deviceInfo.asStateFlow()

    private val _batteryStatus = MutableStateFlow(
        BatteryStatus(
            leftPercent = 100,
            rightPercent = 100,
            casePercent = 72,
            isLeftActive = true,
            isRightActive = true,
            isCharging = true,
            estimatedChargeTimeMinutes = 45,
            lastUpdated = "هم‌اکنون"
        )
    )
    val batteryStatus: StateFlow<BatteryStatus> = _batteryStatus.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<AirPodsDeviceInfo>>(emptyList())
    val discoveredDevices: StateFlow<List<AirPodsDeviceInfo>> = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private var headsetProfile: BluetoothProfile? = null
    private val handler = Handler(Looper.getMainLooper())

    private val profileListener = object : BluetoothProfile.ServiceListener {
        @SuppressLint("MissingPermission")
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                headsetProfile = proxy
                checkConnectedHeadset()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                headsetProfile = null
            }
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { handleDeviceConnected(it) }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    _deviceInfo.value = _deviceInfo.value.copy(isConnected = false)
                    _batteryStatus.value = _batteryStatus.value.copy(
                        isLeftActive = false,
                        isRightActive = false
                    )
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                    device?.let { handleDiscoveredDevice(it, rssi) }
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    if (state == BluetoothAdapter.STATE_ON) {
                        if (_isScanning.value) {
                            handler.postDelayed({ reallyStartScan() }, 800)
                        }
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    }
                }
            }
        }
    }

    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            handleBleResult(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { handleBleResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.w("BTManager", "BLE scan failed: $errorCode")
        }
    }

    init {
        registerReceivers()
        setupProfileProxy()
        inspectInitialState()
    }

    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        try {
            context.registerReceiver(bluetoothReceiver, filter)
        } catch (e: Exception) {
            Log.e("BTManager", "Receiver error: ${e.message}")
        }
    }

    private fun setupProfileProxy() {
        try {
            bluetoothAdapter?.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET)
        } catch (e: Exception) {
            Log.e("BTManager", "Profile proxy error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun inspectInitialState() {
        val adapter = bluetoothAdapter ?: return
        if (!adapter.isEnabled) return

        try {
            val bonded = adapter.bondedDevices
            val airpod = bonded.firstOrNull {
                it.name?.contains("AirPod", ignoreCase = true) == true ||
                        it.name?.contains("AirPods", ignoreCase = true) == true
            }
            if (airpod != null) {
                _deviceInfo.value = AirPodsDeviceInfo(
                    name = airpod.name ?: "AirPods Pro",
                    address = airpod.address,
                    isConnected = true,
                    modelDescription = "Apple AirPods Pro"
                )
                _connectionStatus.value = ConnectionStatus.CONNECTED
                _batteryStatus.value = _batteryStatus.value.copy(
                    isLeftActive = true,
                    isRightActive = true
                )
            }
        } catch (e: Exception) {
            Log.w("BTManager", "Initial state error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkConnectedHeadset() {
        try {
            val proxy = headsetProfile ?: return
            val devices = proxy.connectedDevices
            val active = devices.firstOrNull {
                it.name?.contains("AirPod", ignoreCase = true) == true
            }
            if (active != null) {
                _deviceInfo.value = AirPodsDeviceInfo(
                    name = active.name ?: "AirPods Pro",
                    address = active.address,
                    isConnected = true,
                    modelDescription = "Apple AirPods Pro"
                )
                _connectionStatus.value = ConnectionStatus.CONNECTED
                _batteryStatus.value = _batteryStatus.value.copy(
                    isLeftActive = true,
                    isRightActive = true
                )
            }
        } catch (e: Exception) {
            Log.w("BTManager", "Check headset error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleDeviceConnected(device: BluetoothDevice) {
        val name = device.name ?: "AirPods Pro"
        _deviceInfo.value = AirPodsDeviceInfo(
            name = name,
            address = device.address,
            isConnected = true,
            modelDescription = "Apple AirPods Pro"
        )
        _connectionStatus.value = ConnectionStatus.CONNECTED
        _batteryStatus.value = _batteryStatus.value.copy(
            isLeftActive = true,
            isRightActive = true
        )
    }

    @SuppressLint("MissingPermission")
    private fun handleDiscoveredDevice(device: BluetoothDevice, rssi: Int) {
        val name = device.name ?: return
        val existing = _discoveredDevices.value.toMutableList()
        val index = existing.indexOfFirst { it.address == device.address }
        val item = AirPodsDeviceInfo(
            name = name,
            address = device.address,
            isConnected = false,
            rssi = rssi,
            modelDescription = "Apple AirPods Pro"
        )
        if (index >= 0) existing[index] = item else existing.add(item)
        _discoveredDevices.value = existing

        if (rssi > -65 && (name.contains("AirPod", ignoreCase = true) || name.contains("AirPods", ignoreCase = true))) {
            _deviceInfo.value = item.copy(isConnected = true)
            _connectionStatus.value = ConnectionStatus.CONNECTED
            _batteryStatus.value = _batteryStatus.value.copy(
                isLeftActive = true,
                isRightActive = true
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleBleResult(result: ScanResult) {
        val device = result.device
        val rssi = result.rssi
        val scanRecord = result.scanRecord

        // Apple Manufacturer ID = 0x004C
        val appleData = scanRecord?.getManufacturerSpecificData(0x004C)
        if (appleData != null && appleData.size >= 10) {
            if (appleData[0] == 0x07.toByte()) {
                parseAppleBeaconBattery(appleData, rssi)
            }
        }

        handleDiscoveredDevice(device, rssi)
    }

    private fun parseAppleBeaconBattery(data: ByteArray, rssi: Int) {
        try {
            val leftRaw = data[6].toInt() and 0x0F
            val rightRaw = data[7].toInt() and 0x0F
            val caseRaw = data[8].toInt() and 0x0F

            val leftPct = if (leftRaw in 0..10) leftRaw * 10 else null
            val rightPct = if (rightRaw in 0..10) rightRaw * 10 else null
            val casePct = if (caseRaw in 0..10) caseRaw * 10 else null

            val leftCharging = (data[6].toInt() and 0x80) != 0
            val rightCharging = (data[7].toInt() and 0x80) != 0
            val caseCharging = (data[8].toInt() and 0x80) != 0
            val isCharging = leftCharging || rightCharging || caseCharging

            val leftActive = leftPct != null && rssi > -75
            val rightActive = rightPct != null && rssi > -75

            _batteryStatus.value = BatteryStatus(
                leftPercent = leftPct ?: _batteryStatus.value.leftPercent,
                rightPercent = rightPct ?: _batteryStatus.value.rightPercent,
                casePercent = casePct ?: _batteryStatus.value.casePercent,
                isLeftActive = leftActive,
                isRightActive = rightActive,
                isCharging = isCharging,
                lastUpdated = "هم‌اکنون"
            )
        } catch (e: Exception) {
            Log.w("BTManager", "Battery parse error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val adapter = bluetoothAdapter ?: run {
            _connectionStatus.value = ConnectionStatus.PERMISSION_REQUIRED
            return
        }

        _isScanning.value = true
        _connectionStatus.value = ConnectionStatus.SCANNING
        _discoveredDevices.value = emptyList()

        if (!adapter.isEnabled) {
            try {
                adapter.enable()
                return
            } catch (e: Exception) {
                Log.e("BTManager", "Cannot enable Bluetooth: ${e.message}")
                _isScanning.value = false
                return
            }
        }

        reallyStartScan()
    }

    @SuppressLint("MissingPermission")
    private fun reallyStartScan() {
        val adapter = bluetoothAdapter ?: return
        try {
            if (adapter.isDiscovering) adapter.cancelDiscovery()
            adapter.startDiscovery()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val scanner = adapter.bluetoothLeScanner
                scanner?.startScan(bleScanCallback)
            }
        } catch (e: Exception) {
            Log.w("BTManager", "Start scan error: ${e.message}")
        }

        handler.postDelayed({
            if (_isScanning.value) stopScan()
        }, 12000)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        val adapter = bluetoothAdapter ?: return
        _isScanning.value = false
        if (_connectionStatus.value == ConnectionStatus.SCANNING) {
            _connectionStatus.value = if (_deviceInfo.value.isConnected)
                ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                adapter.bluetoothLeScanner?.stopScan(bleScanCallback)
            }
            if (adapter.isDiscovering) adapter.cancelDiscovery()
        } catch (e: Exception) {
            Log.w("BTManager", "Stop scan error: ${e.message}")
        }
    }

    fun release() {
        stopScan()
        try {
            context.unregisterReceiver(bluetoothReceiver)
        } catch (_: Exception) {}
        try {
            bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, headsetProfile)
        } catch (_: Exception) {}
    }
}
