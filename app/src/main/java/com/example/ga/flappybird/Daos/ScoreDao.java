package com.example.ga.flappybird.Daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ga.flappybird.model.Score;

import java.util.List;

@Dao
public interface ScoreDao {

    @Query("Select * from score")
    List<Score> getAll();

    @Query("Select * from score where email In (:userIds)")
    List<Score> loadAllByIds(int[] userIds);

    @Query("Select * from score where email = :first limit 1")
    Score findByEmail(String first);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Score> scores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Score score);

    @Delete
    void delete(Score score);

    @Query("DELETE FROM score")
    void nukeTable();
}
