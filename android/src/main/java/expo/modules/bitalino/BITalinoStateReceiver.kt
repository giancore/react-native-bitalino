package expo.modules.bitalino

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import info.plux.pluxapi.Constants
import info.plux.pluxapi.bitalino.BITalinoDescription
import info.plux.pluxapi.bitalino.BITalinoState

class BITalinoStateReceiver(private val sendEvent: (name: String, body: Bundle) -> Unit) :
        BroadcastReceiver() {

    private val TAG = "BITalinoStateReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action

        when (action) {
            Constants.ACTION_STATE_CHANGED -> {
                try {
                    val stateId = intent.getIntExtra(Constants.EXTRA_STATE_CHANGED, -1)
                    val event =
                            Bundle().apply {
                                putInt("stateId", stateId)
                                putString("state", getStateName(stateId))
                            }
                    sendEvent("Expo.onBITalinoStateChanged", event)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing state change", e)
                }
            }
            Constants.ACTION_COMMAND_REPLY -> {
                try {
                    val reply =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getParcelableExtra(
                                        Constants.EXTRA_COMMAND_REPLY,
                                        Parcelable::class.java
                                )
                            } else {
                                intent.getParcelableExtra<Parcelable>(Constants.EXTRA_COMMAND_REPLY)
                            }

                    when (reply) {
                        is BITalinoState -> {
                            val event =
                                    Bundle().apply {
                                        putString("type", "state")
                                        putBundle("data", reply.toBundle())
                                    }
                            sendEvent("Expo.onBITalinoCommandReply", event)
                        }
                        is BITalinoDescription -> {
                            val event =
                                    Bundle().apply {
                                        putString("type", "description")
                                        putBoolean("isBITalino2", reply.isBITalino2)
                                        putFloat("firmwareVersion", reply.fwVersion)
                                    }
                            sendEvent("Expo.onBITalinoCommandReply", event)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing command reply", e)
                }
            }
        }
    }

    private fun getStateName(stateId: Int): String {
        return when (stateId) {
            0 -> "NO_CONNECTION"
            1 -> "LISTEN"
            2 -> "CONNECTING"
            3 -> "CONNECTED"
            4 -> "ACQUISITION_TRYING"
            5 -> "ACQUISITION_OK"
            6 -> "ACQUISITION_STOPPING"
            7 -> "DISCONNECTED"
            8 -> "ENDED"
            else -> "UNKNOWN"
        }
    }

    private fun BITalinoState.toBundle() =
            Bundle().apply {
                putString("identifier", identifier)
                putIntArray(
                        "analog",
                        intArrayOf(
                                getAnalog(0),
                                getAnalog(1),
                                getAnalog(2),
                                getAnalog(3),
                                getAnalog(4),
                                getAnalog(5)
                        )
                )
                putInt("battery", battery)
                putInt("batThreshold", batThreshold)
                putIntArray(
                        "digital",
                        intArrayOf(getDigital(0), getDigital(1), getDigital(2), getDigital(3))
                )
            }
}
