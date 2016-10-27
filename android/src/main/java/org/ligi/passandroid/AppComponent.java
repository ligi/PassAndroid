package org.ligi.passandroid;

import dagger.Component;
import javax.inject.Singleton;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.ui.PassAdapter;
import org.ligi.passandroid.ui.PassAndroidActivity;
import org.ligi.passandroid.ui.PassEditActivity;
import org.ligi.passandroid.ui.PassImportActivity;
import org.ligi.passandroid.ui.PassListActivity;
import org.ligi.passandroid.ui.PassListFragment;
import org.ligi.passandroid.ui.PassMenuOptions;
import org.ligi.passandroid.ui.PassNavigationView;
import org.ligi.passandroid.ui.PassViewActivityBase;
import org.ligi.passandroid.ui.SearchPassesIntentService;
import org.ligi.passandroid.ui.TouchImageActivity;
import org.ligi.passandroid.ui.edit.PassandroidFragment;
import org.ligi.passandroid.ui.quirk_fix.USAirwaysLoadActivity;

@Singleton
@Component(modules = {AppModule.class , TrackerModule.class})
public interface AppComponent {

    void inject(PassViewActivityBase passViewActivityBase);

    void inject(PassListActivity passListActivity);

    void inject(PassEditActivity passEditActivity);

    void inject(PassandroidFragment passandroidFragment);

    void inject(PassAdapter passAdapter);

    void inject(PassImportActivity passImportActivity);

    void inject(PassMenuOptions passMenuOptions);

    void inject(SearchPassesIntentService searchPassesIntentService);

    void inject(USAirwaysLoadActivity usAirwaysLoadActivity);

    void inject(PassAndroidActivity passAndroidActivity);

    void inject(PassListFragment passListFragment);

    void inject(PassNavigationView passNavigationView);

    void inject(TouchImageActivity touchImageActivity);

    PassStore passStore();

    Tracker tracker();

    Settings settings();
}
