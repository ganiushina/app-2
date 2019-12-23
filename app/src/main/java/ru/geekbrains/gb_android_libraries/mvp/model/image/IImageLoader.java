package ru.geekbrains.gb_android_libraries.mvp.model.image;


public interface IImageLoader<T> {
    void loadInto(String url, T container);
}
