package ru.geekbrains.gb_android_libraries.mvp.model.entity.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUser extends RealmObject {

    @PrimaryKey
    private String login;
    private String avatarUrl;
    private String reposUrl;
    private RealmList<RealmRepository> repos = new RealmList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public RealmList<RealmRepository> getRepos() {
        return repos;
    }

    public void setRepos(RealmList<RealmRepository> repos) {
        this.repos = repos;
    }
}
