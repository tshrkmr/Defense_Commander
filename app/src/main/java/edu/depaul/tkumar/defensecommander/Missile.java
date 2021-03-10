package edu.depaul.tkumar.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "Plane";
    private final boolean hit = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;
        imageView = new ImageView(mainActivity);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));
    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(() -> imageView.setImageResource(drawId));

        int startX = (int) (Math.random() * screenWidth);
        int endX = (int)((Math.random() * screenWidth));
        int startY = -100;
        //startX = startX - imageView.getDrawable().getIntrinsicWidth();
        //startY = startY - imageView.getDrawable().getIntrinsicWidth();

        float angle = MainActivity.calculateAngle(startX, startY, endX, screenHeight);
        imageView.setRotation(angle);
        imageView.setZ(-10);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", -100, (screenHeight));

        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);
//        yAnim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mainActivity.runOnUiThread(() -> {
//                    if (!hit) {
//                        mainActivity.getLayout().removeView(imageView);
//                        mainActivity.removePlane(Missile.this);
//                    }
//                    Log.d(TAG, "run: NUM VIEWS " +
//                            mainActivity.getLayout().getChildCount());
//                });
//            }
//        });

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(getY()>(screenHeight*.85)){
                    yAnim.cancel();
                    xAnim.cancel();
                    makeGroundBlast();
                    mainActivity.getLayout().removeView(imageView);
                    mainActivity.removePlane(Missile.this);
                }
            }
        });
        aSet.playTogether(xAnim, yAnim);
        return aSet;
    }

    private void makeGroundBlast(){
        final ImageView blast = new ImageView(mainActivity);
        blast.setImageResource(R.drawable.explode);
        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);
        blast.setX(getX()-offset);
        blast.setY(getY()-offset);
        blast.setRotation((float)(360.0*Math.random()));
        aSet.cancel();
        blast.setZ(-15);
        SoundPlayer.getInstance().start("missile_miss");
        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(blast);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(blast, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(blast);
            }
        });
        alpha.start();
        mainActivity.runOnUiThread(()->mainActivity.applyMissileBlast(blast.getX(), blast.getY()));
    }

    void interceptorBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }
}
