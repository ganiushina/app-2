package ru.geekbrains.gb_android_libraries.mvp.model.entity.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.geekbrains.gb_android_libraries.mvp.model.entity.room.RoomRepository;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RepositoryDao {

    @Insert(onConflict = REPLACE)
    void insert(RoomRepository user);

    @Insert(onConflict = REPLACE)
    void insert(RoomRepository... user);

    @Insert(onConflict = REPLACE)
    void insert(List<RoomRepository> user);

    @Update
    void update(RoomRepository user);

    @Insert
    void update(RoomRepository... user);

    @Insert
    void update(List<RoomRepository> user);

    @Delete
    void delete(RoomRepository user);

    @Delete
    void delete(RoomRepository... user);

    @Delete
    void delete(List<RoomRepository> user);

    @Query("SELECT * FROM roomrepository")
    List<RoomRepository> getAll();

    @Query("SELECT * FROM roomrepository WHERE userLogin = :login")
    List<RoomRepository> findForUser(String login);
}
