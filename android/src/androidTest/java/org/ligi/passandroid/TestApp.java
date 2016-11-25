package org.ligi.passandroid;

import org.ligi.passandroid.model.PassStore;

public class TestApp extends App {

    @Override
    public AppComponent createComponent() {
        return DaggerTestComponent.builder().testModule(new TestModule()).build();
    }

    public static TestComponent component() {
        return (TestComponent) App.component();
    }

    @Override
    public void installLeakCanary() {

    }

    public static PassStore getPassStore() {
        return component().passStore();
    }

    public static void reset() {
        setComponent(DaggerTestComponent.builder().testModule(new TestModule()).build());
    }
}
