package ru.geekbrains.gb_android_libraries.mvp.model.repo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import ru.geekbrains.gb_android_libraries.mvp.model.api.ApiHolder;
import ru.geekbrains.gb_android_libraries.mvp.model.api.INetworkStatus;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.Repository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.User;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.realm.RealmRepository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.realm.RealmUser;
import ru.geekbrains.gb_android_libraries.ui.NetworkStatus;

public class RealmUsersRepo {

    INetworkStatus networkStatus = new NetworkStatus();

    public Single<User> getUser(String username) {
        if (networkStatus.isOnline()) {
            return ApiHolder.getApi()
                    .getUser(username)
                    .subscribeOn(Schedulers.io())
                    .map(user -> {
                        Realm realm = Realm.getDefaultInstance();
                        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
                        if(realmUser == null){
                            realm.executeTransaction(innerRealm -> {
                                RealmUser newRealmUser = innerRealm.createObject(RealmUser.class, username);
                                newRealmUser.setAvatarUrl(user.getAvatarUrl());
                                newRealmUser.setReposUrl(user.getReposUrl());
                            });
                        } else {
                            realm.executeTransaction(innerRealm -> {
                                realmUser.setAvatarUrl(user.getAvatarUrl());
                                realmUser.setReposUrl(user.getReposUrl());
                            });
                        }
                        realm.close();
                        return user;
                    });
        } else {
            return Single.create(emitter -> {
                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
                if (realmUser == null) {
                    emitter.onError(new RuntimeException("No such user in cache"));
                } else {
                    emitter.onSuccess(new User(realmUser.getLogin(), realmUser.getAvatarUrl(), realmUser.getReposUrl()));
                }
                realm.close();
            }).subscribeOn(Schedulers.io()).cast(User.class);
        }
    }

    public Single<List<Repository>> getUserRepos(User user) {
        if (networkStatus.isOnline()) {
            return ApiHolder.getApi()
                    .getUserRepos(user.getReposUrl())
                    .map(repos -> {

                        Realm realm = Realm.getDefaultInstance();
                        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                        if (realmUser == null) {
                            realm.executeTransaction(innerRealm -> {
                                RealmUser newRealmUser = innerRealm.createObject(RealmUser.class,  user.getLogin());
                                newRealmUser.setAvatarUrl(user.getAvatarUrl());
                                newRealmUser.setReposUrl(user.getReposUrl());
                            });
                        }

                        realm.executeTransaction(innerRealm -> {
                            realmUser.getRepos().deleteAllFromRealm();
                            for (Repository repository : repos) {
                                RealmRepository realmRepository = innerRealm.createObject(RealmRepository.class, repository.getId());
                                realmRepository.setName(repository.getName());
                                realmUser.getRepos().add(realmRepository);
                            }
                        });
                        realm.close();
                        return repos;
                    })
                    .subscribeOn(Schedulers.io());
        } else {
            return Single.create(emitter -> {
                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                if (realmUser == null) {
                    emitter.onError(new RuntimeException("No such user in cache"));
                } else  {
                    List<Repository> repos = new ArrayList<>();
                    for (RealmRepository realmRepository : realmUser.getRepos()){
                        repos.add(new Repository(realmRepository.getId(), realmRepository.getName()));
                    }
                    emitter.onSuccess(repos);
                }
            }).subscribeOn(Schedulers.io()).cast((Class<List<Repository>>) (Class) List.class);
        }
    }
}
