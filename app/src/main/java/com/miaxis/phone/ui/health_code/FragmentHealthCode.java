package com.miaxis.phone.ui.health_code;

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
import com.miaxis.phone.App;
import com.miaxis.phone.R;
import com.miaxis.phone.data.entity.MxPerson;
import com.miaxis.phone.databinding.FragmentHealthCodeBinding;

import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentHealthCode extends BaseBindingFragment<FragmentHealthCodeBinding> {

    public static FragmentHealthCode newInstance(String name, String idCardNumber, int status) {
        return new FragmentHealthCode(name, idCardNumber, status);
    }

    private String mName;
    private String mIdCardNumber;
    private int mStatus;

    public FragmentHealthCode(String name, String idCardNumber, int status) {
        this.mName = name;
        this.mIdCardNumber = idCardNumber;
        this.mStatus = status;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_health_code;
    }

    @Override
    protected void initView(@NonNull FragmentHealthCodeBinding binding, @Nullable Bundle savedInstanceState) {
        ViewModelHealthCode viewModel = new ViewModelProvider(this).get(ViewModelHealthCode.class);
        binding.layoutTitle.tvTitle.setText("浙江健康码");
        binding.layoutTitle.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvHide.setOnClickListener(v -> {
            if (binding.tvHide.getText().toString().equals("隐藏")) {
                String name = String.valueOf(mName);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < name.length(); i++) {
                    if (i == 0 || (i == name.length() - 1) && name.length() != 2) {
                        stringBuilder.append(name.charAt(i));
                    } else {
                        stringBuilder.append("*");
                    }
                }
                binding.tvName.setText(stringBuilder);
                binding.tvHide.setText("显示");
            } else {
                binding.tvName.setText(String.valueOf(mName));
                binding.tvHide.setText("隐藏");
            }
        });
        binding.tvName.setText(String.valueOf(mName));
        binding.tvHide.performClick();
        //0绿码  1黄码  2红码
        switch (this.mStatus) {
            case 0://0绿码
                binding.tvStatus.setText("绿码");
                binding.tvStatus.setTextColor(0xFF2FA664);
                binding.tvStatusMessage.setText(R.string.string_green_code);
                break;
            case 1://1黄码
                binding.tvStatus.setText("黄码");
                binding.tvStatus.setTextColor(0xFF2FA664);
                binding.tvStatusMessage.setText(R.string.string_green_code);
                break;
            default://2红码
                binding.tvStatus.setText("红码");
                binding.tvStatus.setTextColor(0xFF2FA664);
                binding.tvStatusMessage.setText(R.string.string_green_code);
                break;
        }

        this.mScreenMode = ScreenUtils.getScreenMode(getContext());
        this.mScreenBrightness = ScreenUtils.getScreenBrightness(getContext());

        Disposable subscribe = Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            showLoading("请稍候", "正在处理中...");
            MxPerson mxPerson = new MxPerson(mName, mIdCardNumber, mStatus);
            mxPerson.codeType = 2;
            String json = new Gson().toJson(mxPerson);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            String encode = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(encode,
                    650, 650, "UTF-8", "H", "1",
                    0xFF2FA664, Color.WHITE, BitmapFactory.decodeResource(getResources(), R.drawable.logo),
                    0.2F, null);
            if (bitmap != null) {
                emitter.onNext(bitmap);
            } else {
                throw new NullPointerException("No bitmap");
            }
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    dismissLoading();
                    binding.ivQr.setVisibility(View.VISIBLE);
                    binding.ivQr.setImageBitmap(bitmap);
                }, throwable -> {
                    dismissLoading();
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        ScreenUtils.setScreenMode(getContext(), 0);
        ScreenUtils.saveScreenBrightness(getContext(), 255);
        ScreenUtils.setScreenBrightness(getActivity(), 255);
    }

    private int mScreenMode;
    private int mScreenBrightness;

    @Override
    public void onPause() {
        super.onPause();
        ScreenUtils.setScreenMode(getContext(), this.mScreenMode);
        ScreenUtils.saveScreenBrightness(getContext(), this.mScreenBrightness);
        ScreenUtils.setScreenBrightness(getActivity(), this.mScreenBrightness);
    }

}