package com.example.ga.flappybird;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ga.flappybird.model.Score;

import java.util.ArrayList;


public class HighscoreAdapter extends ArrayAdapter<Score>
{
    private ArrayList<Score> todos;
    private ArrayList<Score> all;

    private Filter filter;

    public HighscoreAdapter(Context context, ArrayList<Score> notes, ArrayList<Score> complete){
        super(context,0,notes);
        this.todos = notes;
        this.all = complete;
    }

    public Score getItem(int position){
        return todos.get(position);
    }

    public int getCount() {
        return todos.size();
    }

    public View getView(int position, View convertView,ViewGroup parent)
    {
        Score t= getItem(position);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.highscore_item,parent,false);
        }


        TextView userid = (TextView) convertView.findViewById(R.id.user_id);
        userid.setText(t.email);

        TextView sc = (TextView) convertView.findViewById(R.id.user_leaderboard);
        sc.setText("Score : "+t.score);

        TextView up = (TextView) convertView.findViewById(R.id.user_position);
        up.setText("Position : "+ indexOf(t));


        

        return convertView;
    }

    public int indexOf(Score t)
    {
        int index=-1;
        for (int i=0;i<all.size();i++)
        {
            if(all.get(i).email.equalsIgnoreCase(t.email) && all.get(i).score == t.score)
            {
                index = i;
            }
        }

        return index+1;
    }


}
