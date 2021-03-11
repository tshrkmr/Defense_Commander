package edu.depaul.tkumar.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class LeaderboardActivity extends AppCompatActivity {

    private String leaderboardList;
    private TextView listTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listTextView = findViewById(R.id.leaderboardListTextView);

        setUpFullScreen();
        getIntentData();
        checkStatus();
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

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("leaderboardList")) {
            leaderboardList = intent.getStringExtra("leaderboardList");
        }
    }

    private void checkStatus() {
        listTextView.setText(leaderboardList);
    }

    public void exit(View v){
        finish();
        finishAffinity();
        //System.exit(0);
    }
}