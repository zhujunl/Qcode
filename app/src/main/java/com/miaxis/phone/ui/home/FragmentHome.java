package com.miaxis.phone.ui.home;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.widget.ClickableLayout;
import com.miaxis.phone.App;
import com.miaxis.phone.BuildConfig;
import com.miaxis.phone.R;
import com.miaxis.phone.data.Model.PersonModel;
import com.miaxis.phone.data.entity.MxPerson;
import com.miaxis.phone.databinding.FragmentHomeBinding;
import com.miaxis.phone.ui.ctid.FragmentCtid;
import com.miaxis.phone.ui.health_code.FragmentHealthCode;
import com.miaxis.phone.ui.input.FragmentInput;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentHome extends BaseBindingFragment<FragmentHomeBinding> {

    public static FragmentHome newInstance() {
        return new FragmentHome();
    }

    public FragmentHome() {
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(@NonNull FragmentHomeBinding binding, @Nullable Bundle savedInstanceState) {
        binding.layoutVersion.tvAppVersion.setText("版本号：" + BuildConfig.VERSION_NAME);
        binding.layoutTitle.setOnClickListener(new ClickableLayout.OnComboClickListener() {
            @Override
            protected int bindClickTimes() {
                return 6;
            }

            @Override
            protected void onComboClick(View v) {
                replaceParent(R.id.fl_root, FragmentInput.newInstance());
            }
        });
        binding.btCtid.setOnClickListener(v -> {
            showLoading();
            Disposable subscribe = Observable.create((ObservableOnSubscribe<MxPerson>) emitter -> {
                MxPerson mxPerson = PersonModel.FindLast();
                if (mxPerson == null) {
                    throw new NullPointerException("Not found");
                } else {
                    sleep();
                    emitter.onNext(mxPerson);
                }
            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object -> {
                        dismissLoading();
                        replaceParent(R.id.fl_root, FragmentCtid.newInstance(object.name, object.cardNumber));
                    }, throwable -> {
                        dismissLoading();
                        showErrorToast("请先注册");
                    });
        });
        binding.btHealthCode.setOnClickListener(v -> {
            showLoading();
            Disposable subscribe = Observable.create((ObservableOnSubscribe<MxPerson>) emitter -> {
                MxPerson mxPerson = PersonModel.FindLast();
                if (mxPerson == null) {
                    throw new NullPointerException("Not found");
                } else {
                    sleep();
                    emitter.onNext(mxPerson);
                }
            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object -> {
                        dismissLoading();
                        replaceParent(R.id.fl_root, FragmentHealthCode.newInstance(object.name,
                                object.cardNumber, object.codeStatus));
                    }, throwable -> {
                        dismissLoading();
                        showErrorToast("请先注册");
                    });
        });
    }

    private void sleep(){
        SystemClock.sleep( 1000L + new Random().nextInt(3) * 1000L);
    }
}