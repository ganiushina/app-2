package ru.geekbrains.gb_android_libraries.mvp.model.api;


import io.reactivex.Single;
import retrofit2.http.*;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.Repository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.User;

import java.util.List;

public interface IDataSource {
    @GET("/users/{user}")
    Single<User> getUser(@Path("user") String username);

    @GET
    Single<List<Repository>> getUserRepos(@Url String url);
}
