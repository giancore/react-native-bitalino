package expo.modules.bitalino

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
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
internal const val REQUEST_ENABLE_BT = 1
internal const val PERMISSION_REQUEST_LOCATION = 2
internal const val PERMISSION_REQUEST_BLUETOOTH = 3

class ReactNativeBitalinoModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ReactNativeBitalino")

    Events(BLUETOOTH_SCAN_EVENT_NAME, BITALINO_FRAME_EVENT_NAME)

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

    AsyncFunction("scanBitalinoDevices") { scanPeriod: Long ->
      try {
        val idBluetoothOk = checkBluetoothAdapter(context)
        if (!idBluetoothOk) {
          return@AsyncFunction false
        }

        val hasAllPermissions = checkPermissions(context)
        if (!hasAllPermissions) {
          return@AsyncFunction false
        }

        bthDeviceScan = BTHDeviceScan(context)

        bthDeviceScan?.doDiscovery()

        Handler(Looper.getMainLooper()).postDelayed({ bthDeviceScan?.stopScan() }, scanPeriod)
        Thread.sleep(scanPeriod)
        return@AsyncFunction true
      } catch (e: Exception) {
        throw Exception("ReactNativeBitalinoModule: failed to start scan", e)
      }
    }

    Function("connect") { address: String ->
      try {
        bitalino =
            BITalinoCommunicationFactory()
                .getCommunication(
                    Communication.BTH,
                    currentActivity,
                    BitalinoDeviceFrameReceiver(emitEvent)
                )

        return@Function bitalino?.connect(address)
      } catch (e: BITalinoException) {
        throw Exception("ReactNativeBitalinoModule: failed to connect bitalino device", e)
      }
    }

    Function("start") { channels: IntArray, frequency: Int ->
      try {
        return@Function bitalino?.start(channels, frequency)
      } catch (e: BITalinoException) {
        Log.e("TAG", e.printStackTrace().toString())
        throw Exception("ReactNativeBitalinoModule: failed to start acquisition device", e)
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
        return@Function bitalino?.stop()
      } catch (e: BITalinoException) {
        throw Exception("ReactNativeBitalinoModule: failed to stop acquisition device", e)
      }
    }
  }

  private var bitalino: BITalinoCommunication? = null
  private var bthDeviceScan: BTHDeviceScan? = null

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
      Log.e("TAG", "emitEvent: $error")
    }
    Unit
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

    context.registerReceiver(
        scanBluetoothDeviceReceiver,
        IntentFilter(Constants.ACTION_MESSAGE_SCAN)
    )

    accessBroadcastReceivers { add(scanBluetoothDeviceReceiver) }
  }

  private fun checkBluetoothAdapter(context: Context): Boolean {
    val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

    if (bluetoothAdapter == null) {
      Log.d("TAG", "Device does not support Bluetooth")
      return false
    }

    if (bluetoothAdapter.isEnabled == false) {
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
