package ru.geekbrains.gb_android_libraries.mvp.model.entity;

import com.google.gson.annotations.Expose;

public class Repository {
    @Expose
    private String id;
    @Expose
    private String name;

    public Repository(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
