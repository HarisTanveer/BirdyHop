package com.example.ga.flappybird;


import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.ga.flappybird.RoomDB.AppDatabase;
import com.example.ga.flappybird.model.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HighScores extends Fragment {


    List<Score> lis;
    List<Score> all;
    ListView list;
    HighscoreAdapter adapter;
    int index;
    LinearLayout layout;



    private ListView createList(){
        list = new ListView(getContext());

        adapter = new HighscoreAdapter(getContext(),new ArrayList<Score>(lis), new ArrayList<Score>(all));
        list.setAdapter(adapter);
        return list;
    }


    public void createView(){



      layout.addView(createList());
      //  getActivity().setContentView(layout);
    }

    public HighScores() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HighScores newInstance() {
        HighScores fragment = new HighScores();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt("index");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            new getScore().execute();
            return layout;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        onCreate(outState);
    }


    public class getScore extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            asyncUtil();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try
            {
                createView();
            }

            catch (Exception e){
                cancel(true);
                if (isCancelled()){
                    return;
                }
            }
        }
    }


    void asyncUtil() {
        AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "scores").build();
        if (index == 0) {
            lis = db.scoreDao().findByHighest();
        }
        else
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            lis = db.scoreDao().findByEmail(user.getEmail());
        }

        all = db.scoreDao().getAll();

    }

}


