package expo.modules.bitalino

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import info.plux.pluxapi.bitalino.BITalinoFrame
import info.plux.pluxapi.bitalino.bth.OnBITalinoDataAvailable

internal const val FRAME = "expo.modules.bitalino.BitalinoDeviceActivity.Frame"

class BitalinoDeviceFrameReceiver(private val sendEvent: (name: String, body: Bundle) -> Unit) :
        OnBITalinoDataAvailable {

    private val handler =
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(message: Message) {
                    val data = message.data
                    val frame = data.getParcelable<BITalinoFrame>(FRAME)

                    if (frame != null) {
                        val event = Bundle().apply { putParcelable("frame", frame.toBundle()) }

                        sendEvent(BITALINO_FRAME_EVENT_NAME, event)
                    }
                }
            }

    override fun onBITalinoDataAvailable(frame: BITalinoFrame) {
        val message =
                handler.obtainMessage().apply {
                    data = Bundle().apply { putParcelable(FRAME, frame) }
                }

        handler.sendMessage(message)
    }

    private fun BITalinoFrame.toBundle() =
            Bundle().apply {
                putString("identifier", getIdentifier())
                putInt("seq", getSequence())
                putIntArray(
                        "digital",
                        intArrayOf(getDigital(0), getDigital(1), getDigital(2), getDigital(3))
                )
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
            }
}
