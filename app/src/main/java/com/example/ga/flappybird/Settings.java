package com.example.ga.flappybird;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.example.ga.flappybird.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

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

        initview();
    }

    private void initview() {

        ImageView imageView = findViewById(R.id.setting_pic);

        FirebaseUser a= FirebaseAuth.getInstance().getCurrentUser();
        TextView userid = findViewById(R.id.user_id);
        if(a.getEmail()!=null)
        {
            userid.setText(a.getEmail());
        }
        else
        {
            userid.setText(a.getUid());
        }

        Uri uri = a.getPhotoUrl();
        if(uri!=null)
        {
            Glide.with(this)
                    .load(uri).into(imageView);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }


}