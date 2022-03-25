package com.miaxis.phone.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.widget.ClickableLayout;
import com.miaxis.phone.BuildConfig;
import com.miaxis.phone.R;
import com.miaxis.phone.databinding.FragmentHomeBinding;
import com.miaxis.phone.ui.input.FragmentInput;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        binding.layoutVersion.tvAppVersion.setText("版本号："+ BuildConfig.VERSION_NAME);
        binding.layoutTitle.setOnClickListener(new ClickableLayout.OnComboClickListener() {
            @Override
            protected int bindClickTimes() {
                return 6;
            }

            @Override
            protected void onComboClick(View v) {
                Toast.makeText(getContext(), "重复点击", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btCtid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //replaceParent(R.id.fl_root, FragmentCtid.newInstance("哈哈哈哈","342921199911112222"));
                replaceParent(R.id.fl_root, FragmentInput.newInstance(0));
            }
        });
        binding.btHealthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //replaceParent(R.id.fl_root, FragmentHealthCode.newInstance("哈哈哈哈","342921199911112222",0));
                replaceParent(R.id.fl_root, FragmentInput.newInstance(1));
            }
        });

    }




}