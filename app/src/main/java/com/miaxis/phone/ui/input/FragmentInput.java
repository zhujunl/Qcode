package com.miaxis.phone.ui.input;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.phone.App;
import com.miaxis.phone.R;
import com.miaxis.phone.data.Model.PersonModel;
import com.miaxis.phone.data.entity.MxPerson;
import com.miaxis.phone.databinding.FragmentInputBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentInput extends BaseBindingFragment<FragmentInputBinding> {

    public static FragmentInput newInstance() {
        return new FragmentInput();
    }

    //private int tag;//0网证  其他 健康码

    public FragmentInput() {
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_input;
    }

    @Override
    protected void initView(@NonNull FragmentInputBinding binding, @Nullable Bundle savedInstanceState) {
        binding.layoutTitle.tvTitle.setText("身份信息输入");
        binding.layoutTitle.ivBack.setOnClickListener(v -> onBackPressed());
        //binding.btnQuery.setText(tag == 0 ? "网证录入" : "健康码录入(默认绿码)");
        binding.btnQuery.setText("确认录入");
        binding.btnQuery.setOnClickListener(v -> query());
        //if (tag == 0) {
        //            binding.ivFace.setVisibility(View.VISIBLE);
        //            binding.ivFace.setOnClickListener(v -> selectImage());
        //} else {
        //            binding.ivFace.setVisibility(View.INVISIBLE);
        //            binding.ivFace.setOnClickListener(null);
        //}
        binding.ivFace.setVisibility(View.INVISIBLE);
        binding.ivFace.setOnClickListener(null);
    }

    private void query() {
        hideInputMethod();
        String name = binding.etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showErrorToast("请先输入姓名");
            return;
        }
        String idNumber = binding.etIdNumber.getText().toString().trim();
        if (TextUtils.isEmpty(idNumber)) {
            showErrorToast("请先输入身份证号码");
            return;
        }
        if (idNumber.length() != 18) {
            showErrorToast("身份证号码位数不正确");
            return;
        }
        //if (binding.ivFace.getVisibility() == View.VISIBLE && TextUtils.isEmpty(faceString)) {
        //    showErrorToast("请先采集人脸照片");
        //    return;
        //}
        showLoading("请稍候", "正在处理中...");
        Disposable subscribe = Observable.create(emitter -> {
            PersonModel.Save(new MxPerson(name, idNumber, 0));
            emitter.onNext("");
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> {
                    dismissLoading();
                    //                    replaceParent(R.id.fl_root,
                    //                            tag == 0 ? FragmentCtid.newInstance(name, idNumber)
                    //                                    : FragmentHealthCode.newInstance(name, idNumber, 0));
                    showSuccessToast("保存成功");
                    onBackPressed();
                }, throwable -> {
                    dismissLoading();
                    showErrorToast("保存失败");
                });

    }

}