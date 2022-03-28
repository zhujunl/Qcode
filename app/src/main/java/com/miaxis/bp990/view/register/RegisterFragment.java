package com.miaxis.bp990.view.register;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.miaxis.bp990.R;
import com.miaxis.bp990.auxiliary.OnLimitClickHelper;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.databinding.FragmentRegisterBinding;
import com.miaxis.bp990.event.FaceRegisterEvent;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.util.IDCardUtils;
import com.miaxis.bp990.view.register.faceregister.FaceRegisterFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.lifecycle.ViewModelProvider;

public class RegisterFragment extends BaseViewModelFragment<FragmentRegisterBinding,RegisterViewModel> {

    private static RegisterFragment instance;
    private OnFragmentInteractionListener mListener;

    public static RegisterFragment getInstance(){
        if(instance==null){
            instance=new RegisterFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener=(OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    protected int setContentView() {
        return R.layout.fragment_register;
    }

    @Override
    protected RegisterViewModel initViewModel() {
        return new ViewModelProvider(this,getViewModelProviderFactory()).get(RegisterViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.bp990.BR.viewmodel;
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        viewModel.registerFlag.observe(this, aBoolean -> {
            dismissWaitDialog();
            if (aBoolean){
                onBackPressed();
            }
        });
        binding.tvHeader.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(FaceRegisterFragment.getIntance())));
        binding.btnRegister.setOnClickListener(v -> {
            if (viewModel.checkInput()) {
                String tip=IDCardUtils.IDCardValidate(viewModel.number.get());
                if (tip.equals("0")){
                    showWaitDialog("正在注册，请稍候..");
                    viewModel.getCourierByPhone();
                }else {
                    ToastManager.toast(tip, ToastManager.INFO);
                }
            } else {
                ToastManager.toast("请输入全部信息", ToastManager.INFO);
            }
        });
        binding.tvHeath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.heath.set(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed()  {
        mListener.backToStack(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFaceRegisterEvent(FaceRegisterEvent event) {
        viewModel.faceFeatureHint.set("已采集");
        binding.tvHeader.setOnClickListener(null);
        viewModel.setFeatureCache(event.getFeature(),event.getBitmap());
        viewModel.setMaskFeatureCache(event.getMaskFeature());
        viewModel.setHeaderCache(event.getBitmap());
        EventBus.getDefault().removeStickyEvent(event);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}