package com.example.ga.flappybird.RoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ga.flappybird.Daos.MCQDao;
import com.example.ga.flappybird.Daos.ScoreDao;
import com.example.ga.flappybird.model.Converters;
import com.example.ga.flappybird.model.Question;
import com.example.ga.flappybird.model.Score;


@Database(entities =  {Score.class, Question.class}, version=1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;
    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "scores").build();
           // sInstance.populateInitialData();
        }
        return sInstance;
    }

//    @VisibleForTesting
//    public static void switchToInMemory(Context context) {
//        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),SampleDatabase.class).build();
//    }

    public abstract ScoreDao scoreDao();
    public abstract MCQDao mcqDao();
}
