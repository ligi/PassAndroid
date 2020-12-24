package org.ligi.passandroid.scan.events

import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class PassScanEventChannelProvider {
    val channel = ConflatedBroadcastChannel<PassScanEvent>()
}