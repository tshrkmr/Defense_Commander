package edu.depaul.tkumar.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 4500;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.splashTitleImage);
        setUpBackgroundSound();
        setUpFullScreen();
        //openMainActivity();
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

        openMainActivity();
    }



    private void openMainActivity(){
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(()->{
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 0, 1);
            objectAnimator.setDuration(3000);
            objectAnimator.start();
            imageView.setVisibility(View.VISIBLE);
            SoundPlayer.getInstance().start("background");
            setUpSoundPlayer();
            Handler handler1 = new Handler(Looper.myLooper());
            handler1.postDelayed(()->{
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
                // close this activity
                finish();
            }, SPLASH_TIME_OUT);
        }, 1000);
    }

    private void setUpBackgroundSound(){
        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background, true);
    }

    private void setUpSoundPlayer(){
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast, false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile, false);
        SoundPlayer.getInstance().setupSound(this, "missile_miss", R.raw.missile_miss, false);
    }
}