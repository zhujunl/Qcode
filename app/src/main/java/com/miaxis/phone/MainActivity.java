package com.miaxis.phone;

import android.os.Bundle;

import com.miaxis.common.activity.BaseBindingFragmentActivity;
import com.miaxis.phone.databinding.ActivityMainBinding;
import com.miaxis.phone.ui.health_code.FragmentHealthCode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends BaseBindingFragmentActivity<ActivityMainBinding> {

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@NonNull ActivityMainBinding binding, @Nullable Bundle savedInstanceState) {





        replace(R.id.fl_root, FragmentHealthCode.newInstance("哈哈哈哈","342921199911112222",0));
    }

}