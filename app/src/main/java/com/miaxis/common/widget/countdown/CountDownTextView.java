package com.miaxis.common.widget.countdown;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tank
 * @date 2020/9/10 16:57
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CountDownTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CountDownTextView(@NonNull Context context) {
        super(context);
    }

    public CountDownTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    private CountDownListener mCountDownListener;

    public void setCountDownListener(CountDownListener countDownListener) {
        this.mCountDownListener = countDownListener;
    }

    private int start = 60;

    private Handler mHandler;
    private Runnable mRunnable;

    private boolean loopEnable = false;

    public void startLoop(int time) {
        this.loopEnable = true;
        this.start = time;
        setText(start + "秒");
        loop();
    }

    private void loop() {
        if (this.mHandler != null && this.mRunnable != null) {
            this.mHandler.postDelayed(this.mRunnable, 1000);
        }
    }

    public void stopLoop() {
        this.loopEnable = false;
        if (this.mHandler != null && this.mRunnable != null) {
            this.mHandler.removeCallbacks(this.mRunnable);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mHandler = new Handler();
        this.mRunnable = new Runnable() {
            @Override
            public void run() {
                start--;
                setText(start + "秒");
                if (mCountDownListener != null) {
                    mCountDownListener.onCountDownProgress(start);
                }
                if (start > 0) {
                    loop();
                } else {
                    if (mCountDownListener != null) {
                        mCountDownListener.onCountDownStop();
                    }
                }
            }
        };
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE && this.loopEnable) {
            loop();
        } else {
            stopLoop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoop();
        this.mRunnable = null;
        this.mHandler = null;
    }
}
