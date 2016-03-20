package org.ligi.passandroid;

import com.google.zxing.BarcodeFormat;
import com.squareup.otto.Bus;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module
public class TestModule {

    private final List<FiledPass> passList;

    public TestModule() {
        passList = new ArrayList<>();
        final PassImpl pass = new PassImpl();
        pass.setId(UUID.randomUUID().toString());
        pass.setDescription("description");
        pass.setBarCode(new BarCode(BarcodeFormat.AZTEC, "messageprobe"));
        passList.add(pass);

    }
    public TestModule(List<FiledPass> passList) {
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
        when(mock.getSortOrder()).thenReturn(PassSortOrder.DATE_ASC);
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
