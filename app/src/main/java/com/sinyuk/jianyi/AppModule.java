package com.sinyuk.jianyi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.utils.ToastUtils;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sinyuk on 16.6.19.
 */
@Module
public final class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return application;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return application.getSharedPreferences("Yuk", MODE_PRIVATE);
    }

    @Provides
    @Singleton
    RxSharedPreferences providePreferences(SharedPreferences preferences) {
        return RxSharedPreferences.create(preferences);
    }

    @Provides
    @Singleton
    File provideCachePath() {
        return new File(application.getExternalCacheDir(), "network_cache");
    }

    @Provides
    @Singleton
    ToastUtils provideToastUtils() {
        return new ToastUtils(application);
    }

}
