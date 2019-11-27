package com.example.ga.flappybird;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.ga.flappybird.RoomDB.AppDatabase;
import com.example.ga.flappybird.model.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Settings extends AppCompatActivity {
    int user_Score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        new DataTask().execute();
    }

    private void initview() {

        ImageView imageView = findViewById(R.id.setting_pic);

        FirebaseUser a= FirebaseAuth.getInstance().getCurrentUser();
        TextView userid = findViewById(R.id.user_id);
        if(a.getEmail()!="")
        {
            userid.setText(a.getEmail());
        }
        else if(a.getUid()!="")
        {
            userid.setText(a.getUid());
        }
        else if(a.getDisplayName()!="")
        {
            userid.setText(a.getDisplayName());
        }
        else
            userid.setText("Guest User");

        Uri uri = a.getPhotoUrl();
        if(uri!=null)
        {
            Glide.with(this)
                    .load(uri).into(imageView);
        }

        TextView userscore = findViewById(R.id.user_highscore);
        userscore.setText("High Score : "+user_Score);

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    public class DataTask extends AsyncTask<Void,Void, Score> {
        @Override
        protected Score doInBackground(Void... voids) {

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "scores").build();

            FirebaseUser a = FirebaseAuth.getInstance().getCurrentUser();
            if(a.getEmail()!=null)
                return db.scoreDao().findUserHighest(a.getEmail());
            else
                return db.scoreDao().findUserHighest(a.getUid());
        }

        @Override
        protected void onPostExecute(Score score) {
            super.onPostExecute(score);
            //Toast.makeText(getApplicationContext(),""+scores.size(),Toast.LENGTH_SHORT).show();
            if(score !=null)
                user_Score = score.getScore();
            initview();
        }
    }


}