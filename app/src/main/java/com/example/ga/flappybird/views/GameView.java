package com.example.ga.flappybird.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.ga.flappybird.Home;
import com.example.ga.flappybird.R;
import com.example.ga.flappybird.RoomDB.AppDatabase;
import com.example.ga.flappybird.model.Bird;
import com.example.ga.flappybird.model.PipePair;
import com.example.ga.flappybird.model.Question;
import com.example.ga.flappybird.model.Score;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class GameView extends View {

    private final int GRAVITY = 2500;
    private final int X_VELOCITY = 250;
    private final int X_DIST_BETWEEN_PIPES = PipePair.getPipeWidth() * 7;

    private final int PIPE_DEATH_POS = -150;
    private int pipeStartingPosition;

    private Paint paint;
    private Bitmap birdBitMap;
    private Bitmap backgroundBitMap;
    private Bitmap topPipeBitMap;
    private Bitmap botPipeBitMap;

    private Bird bird;

    private LinkedList<PipePair> pipePairsOnScreen;

    private Random random;
    private int upperBound;

    private RectF backgroundRect;

    private int screenHeight;
    private int screenWidth;

    private long lastFrame = -1;

    private boolean isPaused = false;

    private int score = 0;

    List<Question> questionList;
    int index = 0;

    boolean background;
    String birdcolor;
    String topic;

    public GameView(Context context) {

        super(context);
        new getMCQs().execute();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        this.screenHeight = displaymetrics.heightPixels;
        this.screenWidth = displaymetrics.widthPixels;

        random = new Random(System.currentTimeMillis());
        upperBound = screenHeight - PipePair.getPipeOpeningSize();

        this.paint = new Paint();
        this.bird = new Bird();

        initBitmaps();
        resetGameState();
        initSettings();

    }

    private void initSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        background = sharedPref.getBoolean("Night_mode",false);
        if(background)
            this.backgroundBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_night);
        else
            this.backgroundBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        if( sharedPref.getBoolean("change_topic",false))
        {
            topic = sharedPref.getString("topic_select","Game");
        }
        else
        {
            topic = "Game";
        }

        if( sharedPref.getBoolean("Bird_color",false))
        {
            birdcolor = sharedPref.getString("color_select","Default");
            if(birdcolor.equalsIgnoreCase("brown"))
            {
                this.birdBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.newbird2);
            }
            else if(birdcolor.equalsIgnoreCase("yellow"))
            {
                this.birdBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.newbird);
            }
            else
                this.birdBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);

        }
        else
        {
            birdcolor = "Default";
            this.birdBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);

        }

    }

    private void initBitmaps() {

//        this.birdBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.newbird);
       // this.backgroundBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_night);
        this.topPipeBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.obstacle_top);
        this.botPipeBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.obstacle_bottom);

    }

    // called on initial startup
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        screenHeight = h;
        screenWidth = w;

        backgroundRect = new RectF(0, 0, screenWidth, screenHeight);

    }

    private void resetGameState() {

        lastFrame = -1;
       // score = 0;

        bird.resetState();

        pipeStartingPosition = (int)(screenWidth * 1.2);
        pipePairsOnScreen = new LinkedList<PipePair>();

        int startingHeight = random.nextInt(upperBound);
        PipePair firstPipes = new PipePair(pipeStartingPosition, startingHeight, screenHeight);
        pipePairsOnScreen.add(firstPipes);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        drawBackground(canvas);
        bird.drawSelf(canvas, birdBitMap, paint);
        drawPipes(canvas);
        drawScore(canvas);

        if (!isPaused) {

            updatePositions();
            checkCollisions();

        }

        invalidate();
    }

    private void drawBackground(Canvas canvas) {

        canvas.drawBitmap(backgroundBitMap, null, backgroundRect, paint);

    }

    private void drawPipes(Canvas canvas) {

        for (PipePair pipePair : pipePairsOnScreen) {

            pipePair.drawSelf(canvas, topPipeBitMap, botPipeBitMap, paint);

        }

    }

    private void drawScore(Canvas canvas) {

        paint.setColor(Color.YELLOW);
        paint.setTextSize(100);
        canvas.drawText("" + score, 100, 100, paint);

    }

    private void updatePositions() {

        long currentTime = System.currentTimeMillis();

        if (lastFrame != -1) {

            float time = (currentTime - lastFrame) / 1000f; // divide by 1000 to convert to seconds
            bird.updatePosition(time, GRAVITY, X_VELOCITY);
            updatePipesOnScreen(time);

        }

        lastFrame = currentTime;

    }

    private void updatePipesOnScreen(float time) {

        for (int i = 0; i < pipePairsOnScreen.size(); i++) {

            PipePair pointer = pipePairsOnScreen.get(i);
            pointer.updatePosition(time, X_VELOCITY);

            if (!pointer.isPassed() && pointer.getPipeRight() < bird.getOffSet()) {

                pointer.setPassed(true);
                score++;

            }

            if (!pointer.getNextPipeCreated() && (int)pointer.getDistanceTraveled() > X_DIST_BETWEEN_PIPES) {

                int randomHeight = random.nextInt(upperBound);
                pipePairsOnScreen.add(new PipePair(pipeStartingPosition, randomHeight, screenHeight));
                pointer.setNextPipeCreated(true);

            }

            if (pipePairsOnScreen.peek().getXPos() < PIPE_DEATH_POS) {

                pipePairsOnScreen.remove();

            }
        }
    }

    private void checkCollisions() {
        if (bird.getY() > screenHeight || bird.getY() < 0) {

            gameOver();

        }
        RectF birdHitBox = bird.getHitbox();
        for (PipePair pipePair : pipePairsOnScreen) {

            RectF topPipeHitbox = pipePair.getTopHitbox();
            RectF botPipeHitbox = pipePair.getBotHitbox();

            if (RectF.intersects(birdHitBox, topPipeHitbox) ||
                    RectF.intersects(birdHitBox, botPipeHitbox)) {

                gameOver();

            }
        }
    }

    private void gameOver() {

        isPaused = true;
        showScoreDialog();

    }

    private void showScoreDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Your score was: " + score).setTitle(
                "Game over!");

        builder.setPositiveButton("Continue?",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        isPaused = false;
//                        resetGameState();
                        dialog.dismiss();
                        showMCQ();
                    }
                });

        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quitGame();
            }
        });

        builder.setCancelable(false);
        
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void showMCQ() {
        if(questionList!=null) {
            if (index < questionList.size()) {
                final Question question = questionList.get(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //builder.setTitle("Answer a question to continue");
                builder.setTitle(question.question);
                builder.setItems(new CharSequence[] {question.answers.get(0), question.answers.get(1), question.answers.get(2), question.answers.get(3)},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        if(question.correct == 0)
                                        {
                                            isPaused = false;
                                            resetGameState();
                                            dialog.dismiss();
                                        }
                                        else
                                        {
                                            quitGame();
                                        }
                                        break;
                                    case 1:
                                        if(question.correct == 1)
                                        {
                                            isPaused = false;
                                            resetGameState();
                                            dialog.dismiss();
                                        }
                                        else
                                        {
                                            quitGame();
                                        }
                                        break;
                                    case 2:
                                        if(question.correct == 2)
                                        {
                                            isPaused = false;
                                            resetGameState();
                                            dialog.dismiss();
                                        }
                                        else
                                        {
                                            quitGame();
                                        }

                                        break;
                                    case 3:
                                        if(question.correct == 3)
                                        {
                                            isPaused = false;
                                            resetGameState();
                                            dialog.dismiss();
                                        }
                                        else
                                        {
                                            quitGame();
                                        }
                                        break;
                                }
                            }
                        });
                builder.setCancelable(false);
                builder.create().show();
                index++;
            }
            if(index == questionList.size())
                index=0;
        }
    }

    public void quitGame()
    {


        Toast.makeText(getContext(),"Game OVER !!!",Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        Activity activity = (Activity)getContext();
        activity.finish();

        Score s = new Score();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getEmail()!=null)
            s.email = user.getEmail();
        else
            s.email = user.getUid();
        s.score = score;
        if(user.getDisplayName()!=null)
            s.name = user.getDisplayName();
        else
            s.name = "Guest_User"+user.getUid();

        new saveScoreFirebase(s).execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            bird.setVerticalSpeed(-900);

        }

        return true;

    }

    public class saveScoreFirebase extends AsyncTask<Void,Void, Void>
    {
        Score s;
        saveScoreFirebase(Score sc)
        {
            s = sc;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Scores").add(s)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                    {
                        @Override
                        public void onSuccess(DocumentReference documentReference)
                        {
                            Log.d("dasd", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.w("asd", "Error adding document", e);
                }
            });
            return null;
        }
    }


    public class getMCQs extends AsyncTask<Void,Void, List<Question>> {
        @Override
        protected List<Question> doInBackground(Void... voids) {

            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "scores").build();

            return db.mcqDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Question> questions) {
            super.onPostExecute(questions);
            questionList = new ArrayList<>(questions);
            Toast.makeText(getContext(),""+questions.size(),Toast.LENGTH_SHORT).show();
        }
    }


}
