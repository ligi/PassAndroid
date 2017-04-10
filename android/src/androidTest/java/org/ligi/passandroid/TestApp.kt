package org.ligi.passandroid

class TestApp : App() {

    override fun createComponent() = DaggerTestComponent.builder().testModule(TestModule()).build()

    override fun installLeakCanary() = Unit

    companion object {

        val component by lazy { App.component as TestComponent }

        val passStore by lazy { component.passStore() }

        fun reset() {
            App.Companion.component = DaggerTestComponent.builder().testModule(TestModule()).build()
        }
    }
}
