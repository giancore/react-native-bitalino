package expo.modules.bitalino

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import expo.modules.interfaces.permissions.Permissions
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import info.plux.pluxapi.BTHDeviceScan
import info.plux.pluxapi.Communication
import info.plux.pluxapi.Constants
import info.plux.pluxapi.bitalino.BITalinoCommunication
import info.plux.pluxapi.bitalino.BITalinoCommunicationFactory
import info.plux.pluxapi.bitalino.BITalinoException

internal const val BLUETOOTH_SCAN_EVENT_NAME = "Expo.onBluetoothDeviceScanned"
internal const val BITALINO_FRAME_EVENT_NAME = "Expo.onBITalinoDataAvailable"
internal const val BITALINO_STATE_EVENT_NAME = "Expo.onBITalinoStateChanged"
internal const val BITALINO_ERROR_EVENT_NAME = "Expo.onBITalinoError"
internal const val BITALINO_DATA_LOST_EVENT_NAME = "Expo.onBITalinoDataLost"
internal const val BITALINO_COMMAND_REPLY_EVENT_NAME = "Expo.onBITalinoCommandReply"
internal const val REQUEST_ENABLE_BT = 1
internal const val PERMISSION_REQUEST_LOCATION = 2
internal const val PERMISSION_REQUEST_BLUETOOTH = 3

class ReactNativeBitalinoModule : Module() {
  private val TAG = "ReactNativeBitalino"

  override fun definition() = ModuleDefinition {
    Name("ReactNativeBitalino")

    Events(
            BLUETOOTH_SCAN_EVENT_NAME,
            BITALINO_FRAME_EVENT_NAME,
            BITALINO_STATE_EVENT_NAME,
            BITALINO_ERROR_EVENT_NAME,
            BITALINO_DATA_LOST_EVENT_NAME,
            BITALINO_COMMAND_REPLY_EVENT_NAME
    )

    OnCreate { registerBroadcastReceivers(context) }

    OnDestroy {
      unregisterBroadcastReceivers(context)

      if (bthDeviceScan != null) {
        bthDeviceScan?.closeScanReceiver()
      }

      if (bitalino != null) {
        try {
          bitalino?.stop()
          bitalino?.closeReceivers()
          bitalino?.disconnect()
        } catch (e: BITalinoException) {
          e.printStackTrace()
        }
      }
    }

    OnActivityEntersForeground { registerBroadcastReceivers(context) }

    OnActivityEntersBackground { unregisterBroadcastReceivers(context) }

    AsyncFunction("requestPermissionsAsync") { promise: Promise ->
      Permissions.askForPermissionsWithPermissionsManager(
              permissionsManager,
              promise,
              Manifest.permission.BLUETOOTH,
              Manifest.permission.BLUETOOTH_ADMIN,
              Manifest.permission.BLUETOOTH_SCAN,
              Manifest.permission.BLUETOOTH_ADVERTISE,
              Manifest.permission.BLUETOOTH_CONNECT,
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_BACKGROUND_LOCATION,
      )
    }

    AsyncFunction("scanBitalinoDevices") { scanPeriod: Long, timeoutMs: Long? ->
      try {
        logDebug("Starting Bluetooth scan for ${scanPeriod}ms")

        val actualTimeout = timeoutMs ?: scanPeriod

        if (!checkBluetoothAdapter(context)) {
          logError("Bluetooth adapter not available")
          throw Exception("Bluetooth not available")
        }

        if (!checkPermissions(context)) {
          logError("Required permissions not granted")
          throw Exception("Required permissions not granted")
        }

        bthDeviceScan = BTHDeviceScan(context)
        bthDeviceScan?.doDiscovery()

        Handler(Looper.getMainLooper())
                .postDelayed(
                        {
                          bthDeviceScan?.stopScan()
                          logDebug("Bluetooth scan stopped")
                        },
                        actualTimeout
                )

        return@AsyncFunction true
      } catch (e: Exception) {
        logError("Failed to start scan", e)
        throw Exception("Failed to start scan: ${e.message}", e)
      }
    }

    Function("connect") { address: String ->
      try {
        logDebug("Connecting to device: $address")

        if (connectionState != BITalinoConnectionState.DISCONNECTED) {
          logError("Cannot connect: device is not disconnected. Current state: $connectionState")
          throw Exception("Device is not disconnected. Current state: $connectionState")
        }

        connectionState = BITalinoConnectionState.CONNECTING

        bitalino =
                BITalinoCommunicationFactory()
                        .getCommunication(
                                Communication.BTH,
                                currentActivity,
                                BitalinoDeviceFrameReceiver(emitEvent)
                        )

        val result = bitalino?.connect(address) ?: false

        if (result) {
          connectionState = BITalinoConnectionState.CONNECTED
          deviceAddress = address
          logDebug("Connected to device: $address")
        } else {
          connectionState = BITalinoConnectionState.DISCONNECTED
          logError("Failed to connect to device: $address")
        }

        return@Function result
      } catch (e: BITalinoException) {
        connectionState = BITalinoConnectionState.ERROR
        logError("Connection failed", e)
        throw Exception("Failed to connect to device: ${e.message}", e)
      }
    }

    AsyncFunction("connectWithRetry") { address: String, maxRetries: Int ->
      var attempts = 0
      var connected = false

      logDebug("Connecting with retry: $address (max $maxRetries attempts)")

      while (attempts < maxRetries && !connected) {
        try {
          if (connectionState == BITalinoConnectionState.DISCONNECTED) {
            connectionState = BITalinoConnectionState.CONNECTING

            bitalino =
                    BITalinoCommunicationFactory()
                            .getCommunication(
                                    Communication.BTH,
                                    currentActivity,
                                    BitalinoDeviceFrameReceiver(emitEvent)
                            )

            connected = bitalino?.connect(address) ?: false

            if (connected) {
              connectionState = BITalinoConnectionState.CONNECTED
              deviceAddress = address
              logDebug("Connected on attempt ${attempts + 1}")
            } else {
              attempts++
              connectionState = BITalinoConnectionState.DISCONNECTED
              if (attempts < maxRetries) {
                logDebug("Attempt $attempts failed, retrying...")
                Thread.sleep(1000)
              }
            }
          }
        } catch (e: Exception) {
          attempts++
          connectionState = BITalinoConnectionState.DISCONNECTED
          logError("Attempt $attempts failed", e)
          if (attempts >= maxRetries) {
            throw Exception("Failed to connect after $maxRetries attempts: ${e.message}", e)
          }
          Thread.sleep(1000)
        }
      }

      return@AsyncFunction connected
    }

    Function("disconnect") {
      try {
        logDebug("Disconnecting device")

        if (bitalino == null) {
          logError("Cannot disconnect: BITalino not initialized")
          throw Exception("BITalino not initialized")
        }

        if (connectionState == BITalinoConnectionState.ACQUISITION_STARTED) {
          logDebug("Stopping acquisition before disconnect")
          bitalino?.stop()
        }

        bitalino?.disconnect()
        connectionState = BITalinoConnectionState.DISCONNECTED
        deviceAddress = null
        currentSampleRate = 0
        activeChannels = intArrayOf()

        logDebug("Disconnected successfully")
        return@Function true
      } catch (e: BITalinoException) {
        connectionState = BITalinoConnectionState.ERROR
        logError("Disconnect failed", e)
        throw Exception("Failed to disconnect: ${e.message}", e)
      }
    }

    Function("start") { channels: IntArray, frequency: Int ->
      try {
        logDebug(
                "Starting acquisition - channels: ${channels.contentToString()}, frequency: $frequency Hz"
        )

        if (bitalino == null) {
          logError("Cannot start: BITalino not connected")
          throw Exception("BITalino not connected")
        }

        if (connectionState != BITalinoConnectionState.CONNECTED) {
          logError("Cannot start: Invalid state - $connectionState")
          throw Exception(
                  "Invalid state: $connectionState. Must be CONNECTED to start acquisition."
          )
        }

        // Validar sampling rate
        if (frequency !in listOf(1, 10, 100, 1000)) {
          logError("Invalid sampling rate: $frequency")
          throw Exception("Invalid sampling rate: $frequency. Must be 1, 10, 100 or 1000 Hz")
        }

        // Validar canais
        if (channels.isEmpty()) {
          logError("No channels specified")
          throw Exception("At least one channel must be specified")
        }

        if (channels.any { it < 0 || it > 5 }) {
          logError("Invalid channel numbers: ${channels.contentToString()}")
          throw Exception("Invalid channel. Must be between 0 and 5")
        }

        val result = bitalino?.start(channels, frequency) ?: false

        if (result) {
          connectionState = BITalinoConnectionState.ACQUISITION_STARTED
          currentSampleRate = frequency
          activeChannels = channels
          logDebug("Acquisition started successfully")
        } else {
          logError("Failed to start acquisition")
        }

        return@Function result
      } catch (e: BITalinoException) {
        connectionState = BITalinoConnectionState.ERROR
        logError("Start acquisition failed", e)
        throw Exception("Failed to start acquisition: ${e.message}", e)
      }
    }

    Function("state") {
      try {
        return@Function bitalino?.state()
      } catch (e: BITalinoException) {
        Log.e("TAG", e.printStackTrace().toString())
        throw Exception("ReactNativeBitalinoModule: failed get state", e)
      }
    }

    Function("stop") {
      try {
        logDebug("Stopping acquisition")

        if (bitalino == null) {
          logError("Cannot stop: BITalino not initialized")
          throw Exception("BITalino not initialized")
        }

        if (connectionState != BITalinoConnectionState.ACQUISITION_STARTED) {
          logDebug("Acquisition not started, nothing to stop. Current state: $connectionState")
          return@Function false
        }

        val result = bitalino?.stop() ?: false

        if (result) {
          connectionState = BITalinoConnectionState.ACQUISITION_STOPPED
          currentSampleRate = 0
          activeChannels = intArrayOf()
          logDebug("Acquisition stopped successfully")
        } else {
          logError("Failed to stop acquisition")
        }

        return@Function result
      } catch (e: BITalinoException) {
        logError("Stop acquisition failed", e)
        throw Exception("Failed to stop acquisition: ${e.message}", e)
      }
    }

    Function("battery") { threshold: Int ->
      try {
        logDebug("Setting battery threshold: $threshold")

        if (bitalino == null) {
          logError("Cannot set battery threshold: BITalino not connected")
          throw Exception("BITalino not connected")
        }

        // Validate threshold value (0-63)
        if (threshold < 0 || threshold > 63) {
          logError("Invalid battery threshold: $threshold")
          throw Exception("Invalid battery threshold: $threshold. Must be between 0 and 63")
        }

        // Cannot set battery threshold during acquisition
        if (connectionState == BITalinoConnectionState.ACQUISITION_STARTED) {
          logError("Cannot set battery threshold during acquisition")
          throw Exception("Cannot set battery threshold while acquiring data")
        }

        val result = bitalino?.battery(threshold) ?: false

        if (result) {
          logDebug("Battery threshold set successfully: $threshold")
        } else {
          logError("Failed to set battery threshold")
        }

        return@Function result
      } catch (e: BITalinoException) {
        logError("Set battery threshold failed", e)
        throw Exception("Failed to set battery threshold: ${e.message}", e)
      }
    }

    Function("version") {
      try {
        return@Function bitalino?.getVersion()
      } catch (e: BITalinoException) {
        throw Exception("ReactNativeBitalinoModule: failed to get the device's firmware version", e)
      }
    }

    Function("trigger") { digitalChannels: IntArray ->
      try {
        logDebug("Triggering digital channels: ${digitalChannels.contentToString()}")

        if (bitalino == null) {
          logError("Cannot trigger: BITalino not connected")
          throw Exception("BITalino not connected")
        }

        // Validate digital channels array length
        // BITalino (r)evolution: 4 outputs (during acquisition)
        // BITalino 2: 2 outputs (anytime)
        if (digitalChannels.isEmpty()) {
          logError("Digital channels array cannot be empty")
          throw Exception("Digital channels array cannot be empty")
        }

        if (digitalChannels.size > 4) {
          logError("Too many digital channels: ${digitalChannels.size}")
          throw Exception("Maximum 4 digital channels supported")
        }

        // Validate channel values (0 or 1)
        if (digitalChannels.any { it != 0 && it != 1 }) {
          logError("Invalid digital channel values: ${digitalChannels.contentToString()}")
          throw Exception("Digital channel values must be 0 or 1")
        }

        val result = bitalino?.trigger(digitalChannels) ?: false

        if (result) {
          logDebug("Digital channels triggered successfully")
        } else {
          logError("Failed to trigger digital channels")
        }

        return@Function result
      } catch (e: BITalinoException) {
        logError("Trigger failed", e)
        throw Exception("Failed to trigger digital channels: ${e.message}", e)
      }
    }

    Function("pwm") { pwmOutput: Int ->
      try {
        logDebug("Setting PWM output: $pwmOutput")

        if (pwmOutput < 0 || pwmOutput > 255) {
          throw Exception("Invalid PWM value: $pwmOutput. Must be between 0 and 255")
        }

        return@Function bitalino?.pwm(pwmOutput)
      } catch (e: BITalinoException) {
        logError("PWM failed", e)
        throw Exception("Failed to set PWM output: ${e.message}", e)
      }
    }

    Function("isConnected") {
      val connected =
              bitalino != null &&
                      (connectionState == BITalinoConnectionState.CONNECTED ||
                              connectionState == BITalinoConnectionState.ACQUISITION_STARTED)
      return@Function connected
    }

    Function("getState") {
      return@Function connectionState.name
    }

    AsyncFunction("getDeviceInfo") {
      val info =
              Bundle().apply {
                putString("address", deviceAddress ?: "")
                putString("state", connectionState.name)
                putBoolean("isConnected", bitalino != null)
                putInt("sampleRate", currentSampleRate)
                putIntArray("activeChannels", activeChannels)
                putBoolean(
                        "isAcquiring",
                        connectionState == BITalinoConnectionState.ACQUISITION_STARTED
                )
              }
      return@AsyncFunction info
    }

    AsyncFunction("getBattery") {
      try {
        logDebug("Getting battery level")

        if (bitalino == null) {
          logError("Cannot get battery: BITalino not connected")
          throw Exception("BITalino not connected")
        }

        // Request state to get battery level (BITalino 2 only)
        val stateRequested = bitalino?.state() ?: false

        if (!stateRequested) {
          logError("Failed to request device state")
          throw Exception("Failed to request device state. BITalino 2 required.")
        }

        logDebug("Battery state requested. Listen for state event to get battery level.")

        // Note: Battery level will be available in the state event
        // Return success - actual battery value comes through state event listener
        return@AsyncFunction Bundle().apply {
          putBoolean("requested", true)
          putString("message", "Battery state requested. Listen for stateChangedListener event.")
        }
      } catch (e: BITalinoException) {
        logError("Get battery failed", e)
        throw Exception("Failed to get battery level: ${e.message}", e)
      }
    }
  }

  private var bitalino: BITalinoCommunication? = null
  private var bthDeviceScan: BTHDeviceScan? = null
  private var connectionState: BITalinoConnectionState = BITalinoConnectionState.DISCONNECTED
  private var deviceAddress: String? = null
  private var currentSampleRate: Int = 0
  private var activeChannels: IntArray = intArrayOf()

  private val context: Context
    get() = appContext.reactContext ?: throw Exceptions.ReactContextLost()

  private val currentActivity
    get() = appContext.currentActivity ?: throw Exceptions.MissingActivity()

  private val permissionsManager: Permissions
    get() = appContext.permissions ?: throw Exceptions.PermissionsModuleNotFound()

  private val broadcastReceivers = mutableListOf<BroadcastReceiver>()

  private val emitEvent = { name: String, body: Bundle ->
    try {
      this@ReactNativeBitalinoModule.sendEvent(name, body)
    } catch (error: Throwable) {
      logError("emitEvent failed: $name", error)
    }
    Unit
  }

  private fun logDebug(message: String) {
    Log.d(TAG, message)
  }

  private fun logError(message: String, error: Throwable? = null) {
    if (error != null) {
      Log.e(TAG, message, error)
    } else {
      Log.e(TAG, message)
    }
  }

  private inline fun accessBroadcastReceivers(block: MutableList<BroadcastReceiver>.() -> Unit) {
    synchronized(broadcastReceivers) { block.invoke(broadcastReceivers) }
  }

  private fun unregisterBroadcastReceivers(context: Context) {
    accessBroadcastReceivers {
      forEach { context.unregisterReceiver(it) }
      clear()
    }
  }

  private fun registerBroadcastReceivers(context: Context) {
    accessBroadcastReceivers {
      if (isNotEmpty()) {
        return
      }
    }

    val scanBluetoothDeviceReceiver = ScanBluetoothDeviceReceiver(emitEvent)
    val bitalinoStateReceiver = BITalinoStateReceiver(emitEvent)

    context.registerReceiver(
            scanBluetoothDeviceReceiver,
            IntentFilter(Constants.ACTION_MESSAGE_SCAN)
    )

    val stateFilter =
            IntentFilter().apply {
              addAction(Constants.ACTION_STATE_CHANGED)
              addAction(Constants.ACTION_COMMAND_REPLY)
            }

    context.registerReceiver(bitalinoStateReceiver, stateFilter)

    accessBroadcastReceivers {
      add(scanBluetoothDeviceReceiver)
      add(bitalinoStateReceiver)
    }
  }

  private fun checkBluetoothAdapter(context: Context): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
      return false
    }

    if (!bluetoothAdapter.isEnabled) {
      val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
      currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    return true
  }

  private fun checkPermissions(context: Context): Boolean {
    val hasCoarseLocationPermission =
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

    if (!hasCoarseLocationPermission) {
      currentActivity.requestPermissions(
              arrayOf(
                      Manifest.permission.ACCESS_COARSE_LOCATION,
                      Manifest.permission.ACCESS_FINE_LOCATION
              ),
              PERMISSION_REQUEST_LOCATION
      )
    }

    val hasBluetoothScanPermission =
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) ==
                    PackageManager.PERMISSION_GRANTED

    if (!hasBluetoothScanPermission) {
      currentActivity.requestPermissions(
              arrayOf(
                      Manifest.permission.BLUETOOTH,
                      Manifest.permission.BLUETOOTH_ADMIN,
                      Manifest.permission.BLUETOOTH_SCAN,
                      Manifest.permission.BLUETOOTH_CONNECT,
                      Manifest.permission.BLUETOOTH_ADVERTISE
              ),
              PERMISSION_REQUEST_BLUETOOTH
      )
    }

    val hasBackgroundLocationPermission =
            context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

    if (!hasBackgroundLocationPermission) {
      return false
    }

    return true
  }
}
