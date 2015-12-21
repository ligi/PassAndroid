package org.ligi.passandroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.ligi.passandroid.model.AndroidFileSystemPassStore;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    PassStore providePassStore(Settings settings) {
        return new AndroidFileSystemPassStore(app, settings);
    }

    @Singleton
    @Provides
    Settings provideSettings() {
        return new Settings(app);
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }
}
