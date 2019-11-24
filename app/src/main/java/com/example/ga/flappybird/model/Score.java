package com.example.ga.flappybird.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
@Entity
public class Score implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int sid;

    @ColumnInfo(name = "score")
    public int score;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "name")
    public String name;

    public Score()
    {

    }


    public Score( int score, String email,String name) {

        this.score = score;
        this.email = email;
        this.name = name;

    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
