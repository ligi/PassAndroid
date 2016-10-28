package org.ligi.passandroid;

import android.app.Application;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatDelegate;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = createComponent();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        installLeakCanary();
        AndroidThreeTen.init(this);
        initTraceDroid();

        AppCompatDelegate.setDefaultNightMode(component.settings().getNightMode());
    }

    public void installLeakCanary() {
        LeakCanary.install(this);
    }

    public AppComponent createComponent() {
        return DaggerAppComponent.builder().appModule(new AppModule(this)).trackerModule(new TrackerModule(this)).build();
    }

    private void initTraceDroid() {
        TraceDroid.init(this);
        Log.setTAG("PassAndroid");
    }

    public static AppComponent component() {
        return component;
    }

    @VisibleForTesting
    public static void setComponent(AppComponent newComponent) {
        component = newComponent;
    }
}
