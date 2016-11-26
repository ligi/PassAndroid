package org.ligi.passandroid

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import org.ligi.passandroid.json_adapter.ColorAdapter
import org.ligi.passandroid.json_adapter.ZonedTimeAdapter
import org.ligi.passandroid.model.*
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Singleton
    @Provides
    internal fun providePassStore(settings: Settings, moshi: Moshi, bus: EventBus): PassStore {
        return AndroidFileSystemPassStore(app, settings, moshi, bus)
    }

    @Singleton
    @Provides
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(ZonedTimeAdapter())
                .add(ColorAdapter())
                .build()
    }

    @Singleton
    @Provides
    internal fun provideSettings(): Settings {
        return AndroidSettings(app)
    }

    @Singleton
    @Provides
    internal fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Singleton
    @Provides
    internal fun provideBus(): EventBus {
        return EventBus.getDefault()
    }

    @Singleton
    @Provides
    internal fun provideState(): State {
        return State()
    }
}
