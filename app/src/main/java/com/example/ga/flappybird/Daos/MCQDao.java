package com.example.ga.flappybird.Daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ga.flappybird.model.Question;

import java.util.List;

@Dao
public interface MCQDao {

    @Query("Select * from question")
    List<Question> getAll();

    @Query("Select * from question where difficulty = :first")
    List<Question> findByDifficulty(String first);

    @Query("Select * from question where topic = :first")
    List<Question> findByTopic(String first);

    @Query("Select * from question where difficulty = :difficulty AND topic = :topic")
    List<Question> findByTopicAndDifficulty(String difficulty, String topic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> questions);

    @Delete
    void delete(Question question);

    @Query("DELETE FROM question")
    void nukeTable();
}