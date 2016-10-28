package org.ligi.passandroid;

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
}
