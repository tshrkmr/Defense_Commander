package edu.depaul.tkumar.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static edu.depaul.tkumar.defensecommander.MainActivity.screenHeight;
import static edu.depaul.tkumar.defensecommander.MainActivity.screenWidth;

public class CloudScroller {
    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;
    private final int resId;

    CloudScroller(Context context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

//        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
//        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);
//
//        backImageA.setImageBitmap(backBitmapA);
//        backImageB.setImageBitmap(backBitmapB);

        backImageA.setImageResource(resId);
        backImageB.setImageResource(resId);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

//        backImageA.setAlpha((float) 0.25);
//        backImageB.setAlpha((float) 0.25);

        animateBack();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress;
                float b_translationX = width * progress - width;

                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);
                //double alpha = 0.25;
//                backImageA.setAlpha((float)alpha);
//                backImageB.setAlpha((float)alpha);
//                alpha = 0.25+.20;
            }
        });
        animator.start();

        final ObjectAnimator alphaA = ObjectAnimator.ofFloat(backImageA, "alpha", 0.25f, 0.95f);
        //alpha.setInterpolator(new LinearInterpolator());
        alphaA.setRepeatCount(ValueAnimator.INFINITE);
        alphaA.setDuration(duration);
        alphaA.setRepeatMode(ValueAnimator.REVERSE);
        alphaA.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        alphaA.start();

        final ObjectAnimator alphaB = ObjectAnimator.ofFloat(backImageB, "alpha", 0.25f, 0.95f);
        //alpha.setInterpolator(new LinearInterpolator());
        alphaB.setRepeatCount(ValueAnimator.INFINITE);
        alphaB.setDuration(duration);
        alphaB.setRepeatMode(ValueAnimator.REVERSE);
        alphaB.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        alphaB.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
