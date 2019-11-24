package com.example.ga.flappybird.RoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ga.flappybird.Daos.ScoreDao;
import com.example.ga.flappybird.model.Score;


@Database(entities =  {Score.class}, version=1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    @SuppressWarnings("WeakerAccess")
   // public abstract PlaceDao place();

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
//        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
//                SampleDatabase.class).build();
//    }
//




    public abstract ScoreDao scoreDao();



}
