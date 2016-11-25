package org.ligi.passandroid

import org.ligi.trulesk.AppReplacingRunnerBase

class AppReplacingRunner : AppReplacingRunnerBase() {

    override fun testAppClass() = TestApp::class.java

}
