package org.ligi.passandroid;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = { TestModule.class })
public interface TestComponent extends AppComponent {
    void inject(TheFullscreenBarcodeActivity theFullscreenBarcodeActivity);

    void inject(ThePassEditActivity thePassEditActivity);

    void inject(ThePassViewActivity thePassViewActivity);

    void inject(ThePastLocationsStore thePastLocationsStore);

    void inject(TheBarCodeEditing theBarCodeEditing);

    void inject(ThePassListSwiping thePassListSwiping);

    void inject(TheFieldListEditFragment theFieldListEditFragment);
}
