package com.example.ga.flappybird.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@IgnoreExtraProperties
@Entity
public class Question implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    public int qid;

    @ColumnInfo(name = "question")
    public String question;

    @ColumnInfo(name = "topic")
    public String topic;

    @ColumnInfo(name = "difficulty")
    public String difficulty;

    @ColumnInfo(name = "answers")
    public ArrayList<String> answers;
    @ColumnInfo(name = "correct")
    public int correct;


    public Question()
    {

    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
