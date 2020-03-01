package org.ligi.passandroid.scan.events

import org.ligi.passandroid.model.pass.Pass

sealed class PassScanEvent

data class DirectoryProcessed(val dir: String) : PassScanEvent()
data class ScanFinished(val foundPasses: List<Pass>) : PassScanEvent()