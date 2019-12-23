package ru.geekbrains.gb_android_libraries;

import android.app.Application;

import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.room.db.Database;
import timber.log.Timber;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new Timber.DebugTree());
        Paper.init(this);
        Database.create(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

    public static App getInstance(){
        return instance;
    }
}
