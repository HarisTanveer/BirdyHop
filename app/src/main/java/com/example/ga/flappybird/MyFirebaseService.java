package com.example.ga.flappybird;

import android.app.Service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.ga.flappybird.RoomDB.AppDatabase;
import com.example.ga.flappybird.model.Question;
import com.example.ga.flappybird.model.Score;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MyFirebaseService extends Service
{
    List<Score> scores;
    List<Question> questions;

    private final IBinder binder = new LocalBinder();

    @Nullable
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate() { }

    public void getStatus()
    {
        //Toast toast;
       // toast = Toast.makeText(this, "Service running!", Toast.LENGTH_SHORT);
        //toast.show();
    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Toast.makeText(this,"Service starting",Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new Runnable(){
            public void run(){
                DownloadAllScores();
            }
        });

        thread.start();
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder
    {
        public MyFirebaseService getService()
        {
            return MyFirebaseService.this;
        }
    }


    public void DownloadAllScores()
    {
      //  Toast.makeText(this,"Getting Scores",Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Scores").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        if (documentSnapshots.isEmpty())
                        {
                            Log.d(" ", "onSuccess: LIST EMPTY");
                            return;
                        }
                        else
                        {

                             scores = documentSnapshots.toObjects(Score.class);

                            // Add all to your list
                            Thread t=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "scores").build();
                                    db.scoreDao().nukeTable();
                                    db.scoreDao().insertAll(scores);
                                }
                            });
                            t.start();

                            Log.d("", "onSuccess: ");
                        }
                    }


                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void DownloadAllMCQs()
    {
       // Toast.makeText(this,"Getting MCQs",Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MCQs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        if (documentSnapshots.isEmpty())
                        {
                            Log.d(" ", "onSuccess: LIST EMPTY");
                            return;
                        }
                        else
                        {

                            questions = documentSnapshots.toObjects(Question.class);
                            Log.d("MCQ SIZE", ":"+questions.size());
                            //Toast.makeText(getApplicationContext(),"Size :"+ questions.size(),Toast.LENGTH_LONG).show();

                            // Add all to your list
                            Thread t=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "scores").build();
                                    db.mcqDao().nukeTable();
                                    db.mcqDao().insertAll(questions);
                                }
                            });
                            t.start();

                            Log.d("", "onSuccess: ");
                        }
                    }


                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }



}
