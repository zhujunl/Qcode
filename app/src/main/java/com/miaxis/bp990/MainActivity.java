package com.miaxis.bp990;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.manager.CameraManager;
import com.miaxis.bp990.manager.CardManager;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Disposable disposable=Observable.create((ObservableOnSubscribe<Boolean>) e->{
            App.getInstance().initApplication();
            e.onNext(true);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f->{
                    Log.e(TAG,"成功");
                },throwable -> Log.e(TAG,"失败"));
//        initView();

        initCamera();

    }

    private void initView(){
        findViewById(R.id.button).setOnClickListener(v-> {
            Person person=new Person("name","cardnum","facepath","finger1","finger2","aa");
            Disposable disposable=Observable.create((ObservableOnSubscribe<Boolean>) e->{
                PersonManager.getInstance().Save(person);
            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(flag->{
                        Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
                    },throwable->{
                        Toast.makeText(this, "不成功", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void initCard(){
        CardManager.getInstance().init(this, new CardManager.IDCardListener() {
            @Override
            public void onIDCardInitResult(boolean result) {
                Log.e(TAG,"CardManager"+result);
            }

            @Override
            public void onIDCardReceive(IDCardRecord idCardRecord, String message) {
                if (idCardRecord!=null){

                    Log.e(TAG,"CardManager"+idCardRecord);
                }
            }
        });
    }

    RoundTextureView rtvCamera;
    FrameLayout flCamera;
    RoundFrameLayout roundFrameLayout;
    RoundBorderView roundBorderView;
    
    private void initCamera(){
        rtvCamera=findViewById(R.id.rtv_camera);
        flCamera=findViewById(R.id.fl_camera);
        rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            rtvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            ViewGroup.LayoutParams layoutParams = rtvCamera.getLayoutParams();
            layoutParams.width = flCamera.getWidth();
            layoutParams.height = flCamera.getHeight();
            rtvCamera.setLayoutParams(layoutParams);
            rtvCamera.turnRound();
            CameraManager.getInstance().resetRetryTime();
            CameraManager.getInstance().openBackCamera(rtvCamera, cameraListener);
        }
    };

    private CameraManager.OnCameraOpenListener cameraListener = previewSize -> {
        FrameLayout.LayoutParams textureViewLayoutParams = (FrameLayout.LayoutParams) rtvCamera.getLayoutParams();
        int newHeight = textureViewLayoutParams.width * previewSize.width / previewSize.height;
        int newWidth = textureViewLayoutParams.width;

        roundFrameLayout = new RoundFrameLayout(this);
        int sideLength = Math.min(newWidth, newHeight);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sideLength, sideLength);
        roundFrameLayout.setLayoutParams(layoutParams);
        FrameLayout parentView = (FrameLayout) rtvCamera.getParent();
        parentView.removeView(rtvCamera);
        parentView.addView(roundFrameLayout);

        roundFrameLayout.addView(rtvCamera);
        FrameLayout.LayoutParams newTextureViewLayoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        newTextureViewLayoutParams.topMargin = -(newHeight - newWidth) / 2;
        rtvCamera.setLayoutParams(newTextureViewLayoutParams);

        View siblingView = roundFrameLayout != null ? roundFrameLayout : rtvCamera;
        roundBorderView = new RoundBorderView(this);
        ((FrameLayout) siblingView.getParent()).addView(roundBorderView, siblingView.getLayoutParams());

        new Handler(Looper.getMainLooper()).post(() -> {
            roundFrameLayout.setRadius(Math.min(roundFrameLayout.getWidth(), roundFrameLayout.getHeight()) / 2);
            roundFrameLayout.turnRound();
            roundBorderView.setRadius(Math.min(roundBorderView.getWidth(), roundBorderView.getHeight()) / 2);
            roundBorderView.turnRound();
        });
    };
}