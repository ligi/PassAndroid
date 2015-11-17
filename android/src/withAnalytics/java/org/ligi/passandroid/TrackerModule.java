package org.ligi.passandroid;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TrackerModule {

    private final App app;

    public TrackerModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    Tracker provideTracker() {
        return new AnalyticsTracker(app);
    }

}
