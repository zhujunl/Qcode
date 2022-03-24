package com.miaxis.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tank
 * @date 2020/9/10 16:57
 * @des
 * @updateAuthor
 * @updateDes
 */
public class TimeView extends androidx.appcompat.widget.AppCompatTextView {

    //private String strDateFormat = "yyyy-MM-dd HH:mm:ss";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

    public TimeView(@NonNull Context context) {
        super(context);
    }

    public TimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Handler mHandler;
    private Runnable mRunnable;

    private void loop() {
        if (this.mHandler != null && this.mRunnable != null) {
            this.mHandler.postDelayed(this.mRunnable, 1000);
        }
    }

    public void stopLoop() {
        if (this.mHandler != null && this.mRunnable != null) {
            this.mHandler.removeCallbacks(this.mRunnable);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mHandler = new Handler();
        this.mRunnable = this::updateTime;
    }

    private void updateTime() {
        setText(this.simpleTimeFormat.format(new Date()));
        loop();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            setText(this.simpleTimeFormat.format(new Date()));
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
