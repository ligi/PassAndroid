package org.ligi.passandroid;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.ui.NavigationFragment;
import org.ligi.passandroid.ui.PassAdapter;
import org.ligi.passandroid.ui.PassAndroidActivity;
import org.ligi.passandroid.ui.PassEditActivity;
import org.ligi.passandroid.ui.PassImportActivity;
import org.ligi.passandroid.ui.PassListActivity;
import org.ligi.passandroid.ui.PassMenuOptions;
import org.ligi.passandroid.ui.PassViewActivityBase;
import org.ligi.passandroid.ui.SearchPassesIntentService;
import org.ligi.passandroid.ui.edit_fragments.CategoryPickFragment;
import org.ligi.passandroid.ui.edit_fragments.ColorPickFragment;
import org.ligi.passandroid.ui.edit_fragments.MetaDataFragment;
import org.ligi.passandroid.ui.edit_fragments.PassandroidFragment;
import org.ligi.passandroid.ui.quirk_fix.USAirwaysLoadActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class , TrackerModule.class})
public interface AppComponent {

    void inject(PassViewActivityBase passViewActivityBase);

    void inject(NavigationFragment passListActivity);

    void inject(PassListActivity passListActivity);

    void inject(PassEditActivity passEditActivity);

    void inject(ColorPickFragment colorPickFragment);

    void inject(MetaDataFragment metaDataFragment);

    void inject(PassandroidFragment passandroidFragment);

    void inject(CategoryPickFragment categoryPickFragment);

    void inject(PassAdapter passAdapter);

    void inject(PassImportActivity passImportActivity);

    void inject(PassMenuOptions passMenuOptions);

    void inject(SearchPassesIntentService searchPassesIntentService);

    void inject(USAirwaysLoadActivity usAirwaysLoadActivity);

    void inject(PassAndroidActivity passAndroidActivity);

    PassStore passStore();

    Tracker tracker();

    Settings settings();
}
