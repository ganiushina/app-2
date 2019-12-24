package ru.geekbrains.gb_android_libraries.mvp.model.repo;

import io.paperdb.Paper;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import ru.geekbrains.gb_android_libraries.mvp.model.api.ApiHolder;
import ru.geekbrains.gb_android_libraries.mvp.model.api.INetworkStatus;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.Repository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.User;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.realm.RealmRepository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.realm.RealmUser;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.room.RoomRepository;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.room.RoomUser;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.room.db.Database;
import ru.geekbrains.gb_android_libraries.ui.NetworkStatus;

import java.util.ArrayList;
import java.util.List;

public class UsersRepo {
    private ICache cache;

    private INetworkStatus networkStatus = new NetworkStatus();

    public UsersRepo(ICache cache) {
        this.cache = cache;
    }

    private Single<User> getUserByName(String username) {
        return ApiHolder.getApi()
                .getUser(username)
                .subscribeOn(Schedulers.io())
                .map(user -> {
                    if (cache instanceof RealmUsersRepo) {
                        Realm realm = Realm.getDefaultInstance();
                        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
                        if (realmUser == null) {
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
                    }
                    if (cache instanceof RoomUsersRepo) {
                        RoomUser roomUser = Database.getInstance().getUserDao().findByLogin(user.getLogin());
                        if (roomUser == null) {
                            roomUser = new RoomUser(user.getLogin());
                        }
                        roomUser.setAvatarUrl(user.getAvatarUrl());
                        roomUser.setReposUrl(user.getReposUrl());
                        roomUser.setName(user.getName());

                        Database.getInstance().getUserDao().insert(roomUser);
                    }
                    if (cache instanceof PaperUsersRepo) {
                        Paper.book("users").write(username, user);
                    }
                    return user;
                });
    }

    private Single<User> putUser(String username) {
        Single<User> userSingle = null;
        if (cache instanceof RealmUsersRepo || cache instanceof RoomUsersRepo) {
            userSingle = Single.create(emitter -> {
                if (cache instanceof RealmUsersRepo) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
                    if (realmUser == null) {
                        emitter.onError(new RuntimeException("No such user in cache"));
                    } else {
                        emitter.onSuccess(new User(realmUser.getLogin(), realmUser.getAvatarUrl(), realmUser.getReposUrl()));
                    }
                    realm.close();
                }
                if (cache instanceof RoomUsersRepo) {
                    RoomUser roomUser = Database.getInstance().getUserDao().findByLogin(username);
                    if (roomUser == null) {
                        emitter.onError(new RuntimeException("No such user in cache"));
                    } else {
                        emitter.onSuccess(new User(roomUser.getLogin(), roomUser.getAvatarUrl(), roomUser.getReposUrl()));
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .cast(User.class);
        }
        if (cache instanceof PaperUsersRepo) {
            if (!Paper.book("users").contains(username)) {
                return Single.error(new RuntimeException("no such user in cache"));
            }

            return Single.fromCallable(() -> Paper.book("users")
                    .read(username))
                    .subscribeOn(Schedulers.io())
                    .cast(User.class);
        }
        return userSingle;
    }

    public Single<User> getUser(String username) {
        if (networkStatus.isOnline()) {
            return getUserByName(username);
        } else {
            return putUser(username);
        }
    }

    public Single<List<Repository>> findUserRepos(User user) {
        return ApiHolder.getApi()
                .getUserRepos(user.getReposUrl())
                .map(repos -> {
                    if (cache instanceof RealmUsersRepo) {
                        Realm realm = Realm.getDefaultInstance();
                        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                        if (realmUser == null) {
                            realm.executeTransaction(innerRealm -> {
                                RealmUser newRealmUser = innerRealm.createObject(RealmUser.class, user.getLogin());
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
                    }
                    if (cache instanceof RoomUsersRepo) {
                        RoomUser roomUser = Database.getInstance().getUserDao().findByLogin(user.getLogin());
                        if (roomUser == null) {
                            roomUser = new RoomUser(user.getLogin(), user.getAvatarUrl(), user.getReposUrl());
                            Database.getInstance().getUserDao().insert(roomUser);
                        }

                        if (!repos.isEmpty()) {
                            List<RoomRepository> roomRepositories = new ArrayList<>();
                            for (Repository repository : repos) {
                                RoomRepository roomRepository = new RoomRepository(repository.getId(), repository.getName(), user.getLogin());
                                roomRepositories.add(roomRepository);
                            }

                            Database.getInstance()
                                    .getRepositoryDao()
                                    .insert(roomRepositories);
                        }
                    }
                    if (cache instanceof PaperUsersRepo) {
                        Paper.book("repos").write(user.getLogin(), repos);
                    }
                    return repos;
                })
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Repository>> getUserRepos(User user) {
        if (networkStatus.isOnline()) {
            return findUserRepos(user);
        } else {
            return putRepos(user);
        }
    }

    private Single<List<Repository>> putRepos(User user) {
        Single<List<Repository>> listRepos = null;
        if (cache instanceof RealmUsersRepo || cache instanceof RoomUsersRepo) {
            listRepos = Single.create(emitter -> {
                if (cache instanceof RealmUsersRepo) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                    if (realmUser == null) {
                        emitter.onError(new RuntimeException("No such user in cache"));
                    } else {
                        List<Repository> repos = new ArrayList<>();
                        for (RealmRepository realmRepository : realmUser.getRepos()) {
                            repos.add(new Repository(realmRepository.getId(), realmRepository.getName()));
                        }
                        emitter.onSuccess(repos);
                    }
                }
                if (cache instanceof RoomUsersRepo) {
                    RoomUser roomUser = Database.getInstance().getUserDao().findByLogin(user.getLogin());
                    if (roomUser == null) {
                        emitter.onError(new RuntimeException("No such user in cache"));
                    } else {
                        List<RoomRepository> roomRepositories = Database.getInstance().getRepositoryDao().findForUser(user.getLogin());
                        List<Repository> repos = new ArrayList<>();
                        for (RoomRepository roomRepository : roomRepositories) {
                            repos.add(new Repository(roomRepository.getId(), roomRepository.getName()));
                        }
                        emitter.onSuccess(repos);
                    }
                }


            }).subscribeOn(Schedulers.io()).cast((Class<List<Repository>>) (Class) List.class);
        }

        if (cache instanceof PaperUsersRepo) {
            if (!Paper.book("repos").contains(user.getLogin())) {
                return Single.error(new RuntimeException("no repos for such user in cache"));
            }
            listRepos = Single.fromCallable(() -> Paper.book("repos")
                    .read(user.getLogin()))
                    .subscribeOn(Schedulers.io())
                    .cast((Class<List<Repository>>) (Class) List.class);
        }

        return listRepos;
    }
}
