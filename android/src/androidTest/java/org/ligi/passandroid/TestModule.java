package org.ligi.passandroid;

import com.google.zxing.BarcodeFormat;

import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassStore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
}
