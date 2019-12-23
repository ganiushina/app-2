package ru.geekbrains.gb_android_libraries.mvp.view.list;

import android.support.annotation.NonNull;

public interface ReposotoryItemView {
    int getPos();
    void setTitle(@NonNull String title);
}
