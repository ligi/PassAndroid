package org.ligi.passandroid.scan.events

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.ligi.passandroid.scan.events.PassScanEvent

class PassScanEventChannelProvider {
    val channel = ConflatedBroadcastChannel<PassScanEvent>()
}