package ru.geekbrains.gb_android_libraries.mvp.model.entity.room;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = RoomUser.class, parentColumns = "login", childColumns = "userLogin", onDelete = CASCADE))
public class RoomRepository {

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String userLogin;

    public RoomRepository(@NonNull String id, String name, String userLogin) {
        this.id = id;
        this.name = name;
        this.userLogin = userLogin;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
