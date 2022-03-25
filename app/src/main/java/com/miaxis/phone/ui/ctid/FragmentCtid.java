package com.miaxis.phone.ui.ctid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.google.gson.Gson;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.utils.QRCodeUtil;
import com.miaxis.common.utils.ScreenUtils;
import com.miaxis.common.widget.countdown.CountDownListener;
import com.miaxis.phone.App;
import com.miaxis.phone.BuildConfig;
import com.miaxis.phone.R;
import com.miaxis.phone.data.entity.MxPerson;
import com.miaxis.phone.databinding.FragmentCtidBinding;

import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentCtid extends BaseBindingFragment<FragmentCtidBinding> {

    public static FragmentCtid newInstance(String name, String idCardNumber) {
        return new FragmentCtid(name, idCardNumber);
    }

    private String mName;
    private String mIdCardNumber;

    public FragmentCtid(String name, String idCardNumber) {
        this.mName = name;
        this.mIdCardNumber = idCardNumber;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_ctid;
    }

    @Override
    protected void initView(@NonNull FragmentCtidBinding binding, @Nullable Bundle savedInstanceState) {
        ViewModelCtid viewModel = new ViewModelProvider(this).get(ViewModelCtid.class);
        binding.tvTitle.setText("网证二维码");
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.cdtvTime.setCountDownListener(new CountDownListener() {
            @Override
            public void onCountDownProgress(int progress) {

            }

            @Override
            public void onCountDownStop() {
                new AlertDialog.Builder(getContext())
                        .setTitle("信息提示：")
                        .setMessage("网证二维码已过期，是否尝试重新获取？")
                        .setPositiveButton("重新获取", (dialog, which) -> {
                            createQr();
                        })
                        .setNegativeButton("放弃", (dialog, which) -> {
                            dialog.dismiss();
                            onBackPressed();
                        })
                        .create().show();
            }
        });
        this.mScreenMode = ScreenUtils.getScreenMode(getContext());
        this.mScreenBrightness = ScreenUtils.getScreenBrightness(getContext());
        createQr();
    }

    private void createQr(){
        showLoading("请稍候","正在处理中...");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            String json = new Gson().toJson(new MxPerson(mName, mIdCardNumber, -1));
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            String encode = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(encode,
                    650,650,"UTF-8","H","1",
                    Color.BLACK, Color.WHITE, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_ctid_logo),
                    0.2F,null);
            if (bitmap!=null){
                emitter.onNext(bitmap);
            }else {
                throw new NullPointerException("No bitmap");
            }
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    dismissLoading();
                    binding.ivQr.setVisibility(View.VISIBLE);
                    binding.ivQr.setImageBitmap(bitmap);
                    binding.cdtvTime.startLoop(BuildConfig.TIMEOUT);
                }, throwable -> {
                    dismissLoading();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ScreenUtils.setScreenMode(getContext(),0);
        ScreenUtils.saveScreenBrightness(getContext(),255);
        ScreenUtils.setScreenBrightness(getActivity(),255);
    }

    private int mScreenMode;
    private int mScreenBrightness;

    @Override
    public void onPause() {
        super.onPause();
        ScreenUtils.setScreenMode(getContext(),this.mScreenMode);
        ScreenUtils.saveScreenBrightness(getContext(),this.mScreenBrightness);
        ScreenUtils.setScreenBrightness(getActivity(),this.mScreenBrightness);
    }

}