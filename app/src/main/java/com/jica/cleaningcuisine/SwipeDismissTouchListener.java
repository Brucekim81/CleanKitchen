package com.jica.cleaningcuisine;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class SwipeDismissTouchListener implements View.OnTouchListener{
    private final View view;
    private final DismissCallbacks callbacks;
    private final int slop;
    private float downX;
    private boolean swiping;

    public interface DismissCallbacks {
        void onDismiss(View view);
    }

    public SwipeDismissTouchListener(View view, DismissCallbacks callbacks) {
        this.view = view;
        this.callbacks = callbacks;
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        this.slop = vc.getScaledTouchSlop();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (swiping) {
                    swiping = false;
                    if (Math.abs(event.getX() - downX) > view.getWidth() / 4.0) {
                        dismiss();
                    } else {
                        v.animate()
                                .translationX(0)
                                .alpha(1)
                                .setDuration(300)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                    }
                } else {
                    // 터치가 클릭으로 해석될 때 performClick 호출
                    v.performClick();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - downX;
                if (Math.abs(deltaX) > slop) {
                    swiping = true;
                    v.setTranslationX(deltaX);
                    v.setAlpha(Math.max(0f, 1f - Math.abs(deltaX) / view.getWidth()));
                }
                return true;
        }
        return false;
    }
    private void dismiss() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                callbacks.onDismiss(view); // 아이템 삭제 콜백 호출
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        view.startAnimation(animation);
    }
}
