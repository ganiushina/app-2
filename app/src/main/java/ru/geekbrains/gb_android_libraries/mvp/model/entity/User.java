package ru.geekbrains.gb_android_libraries.mvp.model.entity;

import com.google.gson.annotations.Expose;


public class User {

    @Expose
    private String login;
    @Expose
    private String avatarUrl;
    @Expose
    private String reposUrl;
    @Expose
    private String name;

    public User(String login, String avatarUrl, String reposUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.reposUrl = reposUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getName() {
        return name;
    }
}
