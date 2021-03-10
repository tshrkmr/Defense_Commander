package edu.depaul.tkumar.defensecommander;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static int screenHeight;
    public static int screenWidth;
    private ConstraintLayout layout;
    private final ArrayList<ImageView> launcherList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private MissileMaker missileMaker;
    private TextView levelTextView, scoreTextView;
    private int scoreValue;
    double launcher1X, launcher1Y, launcher2X, launcher2Y, launcher3X, launcher3Y;
    private Point launcher1p, launcher2p, launcher3p;
    private final ArrayList<Point> basePoints = new ArrayList<>();
    private static final int Base_BLAST_RANGE = 250;
    public static int interceptorCount = 0;
    public ArrayList<ImageView> interceptorArrayList = new ArrayList<>();
    private ImageView gameOverImageView;
    private String leaderboardResults;
    private String showLeaderboard = "showLeaderboard";
    private String updateScore = "updateScore";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpFullScreen();
        initializeFields();
        getScreenDimensions();
        setUpScrollingBackground();

        layout.setOnTouchListener((view, motionEvent)->{
          if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
              handleTouch(motionEvent.getX(), motionEvent.getY());
          }
          return false;
        });
    }

    private void initializeFields() {
        ImageView launcher1ImageView = findViewById(R.id.mainLauncher1);
        ImageView launcher2ImageView = findViewById(R.id.mainLauncher2);
        ImageView launcher3ImageView = findViewById(R.id.mainLauncher3);
        launcherList.add(launcher1ImageView);
        launcherList.add(launcher2ImageView);
        launcherList.add(launcher3ImageView);
        levelTextView = findViewById(R.id.mainLevelTextView);
        scoreTextView = findViewById(R.id.mainScoreTextView);
        gameOverImageView = findViewById(R.id.mainGameOverImageView);

//        launcher1X = launcher1.getX()+ ((0.5 * launcher1.getWidth()));
//        launcher1Y = launcher1.getY() + ((0.5 * launcher1.getHeight()));
//        launcher2X = launcher2.getX() + ((float) (0.5 * launcher2.getWidth()));
//        launcher2Y = launcher2.getY() + ((float) (0.5 * launcher2.getHeight()));
//        launcher3X = launcher3.getX() + ((float) (0.5 * launcher3.getWidth()));
//        launcher3Y = launcher3.getY() + ((float) (0.5 * launcher3.getHeight()));
//        launcher1p.x = (int) launcher1X;
//        launcher1p.y = (int) launcher1Y;
//        launcher2p.x = (int) launcher2X;
//        launcher2p.y = (int) launcher2Y;
//        launcher3p.x = (int) launcher3X;
//        launcher3p.y = (int) launcher3Y;
//        basePoints.add(launcher1p);
//        basePoints.add(launcher2p);
//        basePoints.add(launcher3p);
    }

    private void setUpFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getScreenDimensions() {
        Display display = this.getDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        display.getRealMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setUpScrollingBackground(){
        layout = findViewById(R.id.layout);

        new CloudScroller(this,
                layout, R.drawable.clouds, 12000);
        if(missileMaker == null) {
            missileMaker = new MissileMaker(this, screenWidth, screenHeight);
            new Thread(missileMaker).start();
        }
    }

    public void handleTouch(float xLoc, float yLoc) {
        Log.d(TAG, "handleTouch: " + xLoc + "," + yLoc);
        ImageView closestBase = findClosestBase(xLoc, yLoc);
        if(!(yLoc>(screenHeight*0.80))) {
            double startX = getStartX(closestBase);
            double startY = getStartY(closestBase);
//                Interceptor i = new Interceptor(this, (float) (startX - 10), (float) (startY - 30), xLoc, yLoc);
            new Interceptor(this, (float) (startX - 10), (float) (startY - 30), xLoc, yLoc);
            SoundPlayer.getInstance().start("launch_interceptor");
                //i.launch();
        }
    }

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void removePlane(Missile m) {
        missileMaker.removeMissile(m);
    }

    public void setLevelTextView(final int value) {
        runOnUiThread(() -> levelTextView.setText(String.format(Locale.getDefault(), "Level: %d", value)));
    }

    public void incrementScore() {
        scoreValue++;
        scoreTextView.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void applyInterceptorBlast(Interceptor interceptor) {
        missileMaker.applyInterceptorBlast(interceptor);
    }

    public void applyMissileBlast(float xLoc, float yLoc){
        ImageView nearestBase = findClosestBase(xLoc, yLoc);
        float distance = distance(getStartX(nearestBase), getStartY(nearestBase), xLoc, yLoc);
        if(distance<Base_BLAST_RANGE){
            launcherList.remove(nearestBase);
            Base.destruct(this, nearestBase);
            if(launcherList.size() == 0)
            {
                gameOver();
            }
        }
    }

    private ImageView findClosestBase(float xLoc, float yLoc){
        float min = Float.MAX_VALUE;
        ImageView returnImageView = new ImageView(this);
        for(ImageView imageView : launcherList) {
            float distance = distance(getStartX(imageView), getStartY(imageView), xLoc, yLoc);
            if(distance<min){
                min = distance;
                returnImageView = imageView;
            }
        }
//        float distanceFromBase1 = distance(getStartX(launcher1), getStartY(launcher1), xLoc, yLoc);
//        float distanceFromBase2 = distance(getStartX(launcher2), getStartY(launcher2), xLoc, yLoc);
//        float distanceFromBase3 = distance(getStartX(launcher3), getStartY(launcher3), xLoc, yLoc);
//
//        float min = distanceFromBase1;
//        ImageView returnImageView = launcher1;
//        if(distanceFromBase2<min) {
//            min = distanceFromBase2;
//            returnImageView = launcher2;
//        }
//        if(distanceFromBase3<min) {
//            min = distanceFromBase3;
//            returnImageView = launcher3;
//        }
        return returnImageView;
    }

    private float distance(double baseX, double baseY, float xLoc, float yLoc){
        return (float) Math.sqrt(((baseX-xLoc)*(baseX-xLoc))+((baseY - yLoc)*(baseY - yLoc)));
    }

    public double getStartX(ImageView imageView){
        return imageView.getX() + (0.5 * imageView.getWidth());
    }

    public double getStartY(ImageView imageView){
        return imageView.getY() + (0.5 * imageView.getHeight());
    }

    private void gameOver(){
        missileMaker.setRunning(false);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(gameOverImageView, "alpha", 0, 1);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
        gameOverImageView.setVisibility(View.VISIBLE);
        ScoreDataBaseHandler scoreDataBaseHandler = new ScoreDataBaseHandler(this, "n/a", 0, 0, "compareScore");
        new Thread(scoreDataBaseHandler).start();
    }

    public void compareScore(int lowestScore, String results){
        leaderboardResults = results;
        if(scoreValue>lowestScore){
            getInitials();
        }else{
            openLeaderboardActivity(showLeaderboard);
        }
    }

    private void getInitials(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_top_score, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initials (up to 3 characters):");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView initialsTextView = view.findViewById(R.id.dialogInitialsTextView);
                String initials = initialsTextView.getText().toString();
                if(initials.equals("") || initials.length()<3){
                    initialsError();
                }else{
                    openLeaderboardActivity(updateScore);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openLeaderboardActivity(showLeaderboard);
            }
        });
    }

    private void initialsError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Try Again");
        builder.setMessage("Initials should be between 0 and 3 characters ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getInitials();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openLeaderboardActivity(String status){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("leaderboardList", leaderboardResults);
        intent.putExtra("status", status);
        startActivity(intent);
    }
}