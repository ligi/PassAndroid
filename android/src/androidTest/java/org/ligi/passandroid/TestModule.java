package org.ligi.passandroid;

import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.model.comparator.PassSortOrder;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module
public class TestModule {

    private final List<Pass> passList;

    public TestModule() {
        passList = new ArrayList<>();
        final PassImpl pass = new PassImpl(UUID.randomUUID().toString());
        pass.setDescription("description");
        pass.setBarCode(new BarCode(PassBarCodeFormat.AZTEC, "messageprobe"));
        passList.add(pass);

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

        for (final Pass pass : passList) {
            fixedPassListPassStore.getClassifier().moveToTopic(pass,"test");
        }
        return fixedPassListPassStore;
    }

    @Singleton
    @Provides
    Settings provideSettings() {
        final Settings mock = mock(Settings.class);
        when(mock.getSortOrder()).thenReturn(PassSortOrder.DATE_ASC);
        when(mock.getPassesDir()).thenReturn(new File(""));
        when(mock.doTraceDroidEmailSend()).thenReturn(false);
        return mock;
    }


    @Singleton
    @Provides
    EventBus provideBus() {
        return mock(EventBus.class);
    }


    @Singleton
    @Provides
    Tracker provideTracker() {
        return mock(Tracker.class);
    }
}
