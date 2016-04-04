package org.ligi.passandroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.json_adapter.ColorAdapter;
import org.ligi.passandroid.json_adapter.ZonedTimeAdapter;
import org.ligi.passandroid.model.AndroidFileSystemPassStore;
import org.ligi.passandroid.model.AndroidSettings;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    PassStore providePassStore(Settings settings, Moshi moshi,EventBus bus) {
        return new AndroidFileSystemPassStore(app, settings, moshi,bus);
    }

    @Singleton
    @Provides
    Moshi provideMoshi() {
        return new Moshi.Builder()
                .add(new ZonedTimeAdapter())
                .add(new ColorAdapter())
                .build();
    }

    @Singleton
    @Provides
    Settings provideSettings() {
        return new AndroidSettings(app);
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Singleton
    @Provides
    EventBus provideBus() {
        return EventBus.getDefault();
    }
}
