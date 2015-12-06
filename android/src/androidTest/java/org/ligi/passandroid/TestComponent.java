package org.ligi.passandroid;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { TestModule.class })
public interface TestComponent extends AppComponent {
    void inject(TheFullscreenBarcodeActivity theFullscreenBarcodeActivity);

    void inject(ThePassEditActivity thePassEditActivity);

    void inject(ThePassViewActivity thePassViewActivity);

    void inject(ThePastLocationsStore thePastLocationsStore);

    void inject(TheBarCodeEditFragment theBarCodeEditFragment);
}
