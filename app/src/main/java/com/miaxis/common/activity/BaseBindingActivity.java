package com.miaxis.common.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.miaxis.common.runnable.SingleRunnable;
import com.miaxis.phone.App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import es.dmoral.toasty.Toasty;

/**
 * @author Tank
 * @date 2021/4/25 3:59 PM
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class BaseBindingActivity<V extends ViewDataBinding> extends AppCompatActivity {

    protected V binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initWindow();
        binding = DataBindingUtil.setContentView(this, initLayout());
        initData(binding, savedInstanceState);
        initView(binding, savedInstanceState);
    }

    protected void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(params);
    }

    protected abstract int initLayout();

    protected abstract void initView(@NonNull V binding, @Nullable Bundle savedInstanceState);

    protected void initData(@NonNull V binding, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideInputMethod();
        }
        return super.onTouchEvent(event);
    }

    //    @Override
    //    public void onWindowFocusChanged(boolean hasFocus) {
    //        super.onWindowFocusChanged(hasFocus);
    //        if (hasFocus) {
    //            hideNavigationBar();
    //        }
    //    }

    public void hideNavigationBar() {
        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(flags);
            }
        });
    }

    public void hideInputMethod() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissLoading();
    }

    private ProgressDialog progressDialog;

    protected void showLoading() {
        runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(BaseBindingActivity.this);
            }
            progressDialog.setTitle("请稍候... ");
            progressDialog.show();
        });
    }

    protected void showLoading(String title, String message) {
        runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(BaseBindingActivity.this);
            }
            progressDialog.setTitle(String.valueOf(title));
            progressDialog.setMessage(String.valueOf(message));
            progressDialog.show();
        });
    }

    protected void dismissLoading() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    public void showToast(String tips) {
        runOnUiThread(new SingleRunnable<String>(tips) {
            @Override
            public void onRun(String data) {
                Toasty.info(App.getInstance(), String.valueOf(data), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void showSuccessToast(String tips) {
        runOnUiThread(new SingleRunnable<String>(tips) {
            @Override
            public void onRun(String data) {
                Toasty.success(App.getInstance(), String.valueOf(data), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void showErrorToast(String tips) {
        runOnUiThread(new SingleRunnable<String>(tips) {
            @Override
            public void onRun(String data) {
                Toasty.error(App.getInstance(), String.valueOf(data), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
