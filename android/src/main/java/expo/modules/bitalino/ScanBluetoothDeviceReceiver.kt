package expo.modules.bitalino

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import info.plux.pluxapi.Constants

class ScanBluetoothDeviceReceiver(private val sendEvent: (name: String, body: Bundle) -> Unit) :
    BroadcastReceiver() {

  private fun onBluetoothDeviceScanned(device: BluetoothDevice) {
    val event = Bundle().apply { putParcelable("device", device.toBundle()) }
    sendEvent(BLUETOOTH_SCAN_EVENT_NAME, event)
  }

  private fun BluetoothDevice.toBundle() =
      Bundle().apply {
        putString("name", name)
        putString("address", address)
      }

  override fun onReceive(context: Context, intent: Intent) {
    val action: String? = intent.action

    when (action) {
      Constants.ACTION_MESSAGE_SCAN -> {
        val bluetoothDevice =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              intent.getParcelableExtra(Constants.EXTRA_DEVICE_SCAN, BluetoothDevice::class.java)
            } else {
              intent.getParcelableExtra<BluetoothDevice>(Constants.EXTRA_DEVICE_SCAN)
            }

        if (bluetoothDevice != null) {
          onBluetoothDeviceScanned(bluetoothDevice)
        }
      }
    }
  }
}
