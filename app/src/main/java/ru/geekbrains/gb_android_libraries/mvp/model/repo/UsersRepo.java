package ru.geekbrains.gb_android_libraries.mvp.model.repo;


import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.gb_android_libraries.mvp.model.api.ApiHolder;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.Repository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.User;

import java.util.List;

public class UsersRepo {

    public UsersRepo(ICache cache){

    }

    public Single<User> getUser(String username) {
        return ApiHolder.getApi().getUser(username).subscribeOn(Schedulers.io());
    }

    public Single<List<Repository>> getUserRepos(String url) {
        return ApiHolder.getApi().getUserRepos(url).subscribeOn(Schedulers.io());
    }
}
