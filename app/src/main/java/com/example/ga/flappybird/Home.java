package com.example.ga.flappybird;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.ga.flappybird.RoomDB.AppDatabase;
import com.example.ga.flappybird.model.Score;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Home extends AppCompatActivity {

    MyFirebaseService myService;
    ServiceConnection connection;
    boolean bound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initStartGameButton();
        initHighScoresButton();
        initLogoutButton();

        //starting firebase service
        connection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                myService = ((MyFirebaseService.LocalBinder) binder).getService();
                bound = true;
                myService.getStatus();
                myService.DownloadAllScores();

            }
            public void onServiceDisconnected(ComponentName className) {
                bound = false;
            }
        };

       // startDataSyncService();
        new DataTask().execute();
    }



    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this,MyFirebaseService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    private void startDataSyncService(){
        SharedPreferences preferences = getSharedPreferences("service",Context.MODE_PRIVATE);
        boolean started = preferences.getBoolean("started",false);
        SharedPreferences.Editor editor = preferences.edit();

        Intent serviceIntent = new Intent(this,MyFirebaseService.class);
        startService(serviceIntent);

        editor.putBoolean("started",true);
        editor.commit();

    }

    private void initLogoutButton() {
        Button logout = (Button)findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent startGameIntent = new Intent(Home.this, LoginActivity.class);
                startActivity(startGameIntent);
                finish();

            }
        });
    }

    private void initStartGameButton() {

        Button startButton = (Button)findViewById(R.id.start_game_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGameIntent = new Intent(Home.this, GameActivity.class);
                startActivity(startGameIntent);
                finish();
            }
        });

    }

    private void initHighScoresButton() {



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public class DataTask extends AsyncTask<Void,Void, List<Score>> {
        @Override
        protected List<Score> doInBackground(Void... voids) {

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "scores").build();

            return db.scoreDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Score> scores) {
            super.onPostExecute(scores);
            Toast.makeText(getApplicationContext(),""+scores.size(),Toast.LENGTH_SHORT).show();
        }
    }
}