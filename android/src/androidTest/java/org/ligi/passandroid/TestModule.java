package org.ligi.passandroid;

import com.google.zxing.BarcodeFormat;
import com.squareup.otto.Bus;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassSortOrder;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module
public class TestModule {

    private final List<Pass> passList;

    public TestModule() {
        passList = new ArrayList<>();
        final PassImpl object = new PassImpl();
        object.setDescription("description");
        object.setBarCode(new BarCode(BarcodeFormat.AZTEC, "messageprobe"));
        passList.add(object);

    }
    public TestModule(List<Pass> passList) {
        this.passList = passList;
    }


    @Singleton
    @Provides
    PassStore providePassStore() {
        final FixedPassListPassStore fixedPassListPassStore = new FixedPassListPassStore(passList);
        if (!passList.isEmpty()) {
            fixedPassListPassStore.setCurrentPass(passList.get(0));
        }
        return fixedPassListPassStore;
    }

    @Singleton
    @Provides
    Settings provideSettings() {
        final Settings mock = mock(Settings.class);
        when(mock.getSortOrder()).thenReturn(PassSortOrder.DATE);
        when(mock.doTraceDroidEmailSend()).thenReturn(false);
        return mock;
    }


    @Singleton
    @Provides
    Bus provideBus() {
        return mock(Bus.class);
    }


    @Singleton
    @Provides
    Tracker provideTracker() {
        return mock(Tracker.class);
    }
}
