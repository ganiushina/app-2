package ru.geekbrains.gb_android_libraries.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.geekbrains.gb_android_libraries.App;
import ru.geekbrains.gb_android_libraries.mvp.model.api.INetworkStatus;

public class NetworkStatus implements INetworkStatus {

    @Override
    public Status getStatus() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return Status.WIFI;

                case ConnectivityManager.TYPE_ETHERNET:
                    return Status.ETHERNET;

                case ConnectivityManager.TYPE_MOBILE:
                    return Status.MOBILE;
            }
            return Status.OTHER;
        }
        return Status.OFFLINE;
    }

    @Override
    public boolean isOnline() {
        return !getStatus().equals(Status.OFFLINE);
    }

    @Override
    public boolean isWifi() {
        return getStatus().equals(Status.WIFI);
    }

    @Override
    public boolean isEthernet() {
        return getStatus().equals(Status.ETHERNET);
    }

    @Override
    public boolean isMobile() {
        return getStatus().equals(Status.MOBILE);
    }

    @Override
    public boolean isOffline() {
        return getStatus().equals(Status.OFFLINE);
    }
}