package edu.depaul.tkumar.defensecommander;

import android.animation.AnimatorSet;

import java.util.ArrayList;

public class MissileMaker implements Runnable {

    private boolean isRunning;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private int missileCount = 0;
    private final int screenWidth;
    private final int screenHeight;
    private final MainActivity mainActivity;
    private static int LEVEL_CHANGE_VALUE = 5; // Change level after this many planes
    private static final int INTERCEPTOR_BLAST_RANGE = 120;
    private int level = 1;
    private long delay = 1000; // Pause between new planes
    private MissileMaker missileMaker;

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight){
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void run() {
        setRunning(true);
        while (isRunning) {
            missileCount++;
            int resId = R.drawable.missile;

            long planeTime = (long) ((delay * 0.5) + (Math.random() * delay));
            final Missile missile = new Missile(screenWidth, screenHeight, planeTime, mainActivity);
            activeMissiles.add(missile);
            SoundPlayer.getInstance().start("launch_missile");
            final AnimatorSet as = missile.setData(resId);

            mainActivity.runOnUiThread(as::start);


            if (missileCount > LEVEL_CHANGE_VALUE) {
                LEVEL_CHANGE_VALUE *= 1.5;
                level++;
                mainActivity.setLevelTextView(level);

                delay -= 200; // Reduce the delay between planes

                if (delay < 200) // But don't let the delay go down to 0
                    delay = 200;

                missileCount = 0;
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void setRunning(boolean running){
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for(Missile m : temp){
            m.stop();
        }
    }

    void removeMissile(Missile m) {
        activeMissiles.remove(m);
    }

    void applyInterceptorBlast(Interceptor interceptor) {

        float interceptorX = interceptor.getX();
        float interceptorY = interceptor.getY();

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp) {
            float planeX = (int) (m.getX() + (0.5 * m.getWidth()));
            float planeY = (int) (m.getY() + (0.5 * m.getHeight()));
            float distanceBetween = (float) Math.sqrt((planeY - interceptorY) * (planeY - interceptorY) + (planeX - interceptorX) * (planeX - interceptorX));

            if (distanceBetween < INTERCEPTOR_BLAST_RANGE) {
                SoundPlayer.getInstance().start("interceptor_hit_missile");
                mainActivity.incrementScore();
                m.interceptorBlast(planeX, planeY);
                nowGone.add(m);
            }
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }
}
