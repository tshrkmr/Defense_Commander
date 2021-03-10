package edu.depaul.tkumar.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.provider.ContactsContract;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Base {

    public static void destruct(MainActivity mainActivity, ImageView imageView){
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(imageView);
        final ImageView blast = new ImageView(mainActivity);
        blast.setImageResource(R.drawable.blast);
        blast.setX(imageView.getX());
        blast.setY(imageView.getY());
        mainActivity.getLayout().addView(blast);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(blast, "alpha", 0.0f);
        //alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(blast);
            }
        });
        alpha.start();
    }
}
