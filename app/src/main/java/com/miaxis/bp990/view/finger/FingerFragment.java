package com.miaxis.bp990.view.finger;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentFingerBinding;
import com.miaxis.bp990.event.FingerRegisterEvent;
import com.miaxis.bp990.view.face.FaceFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/24 19:23
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FingerFragment extends BaseViewModelFragment<FragmentFingerBinding,FingerViewModel> {

    private static FingerFragment instance;
    private OnFragmentInteractionListener mListener;
    private Person person;

    public static FingerFragment getInstance(){
        if(instance==null){
            instance=new FingerFragment();
        }
        return instance;
    }

    public static FingerFragment getInstance(Person person){
        if(instance==null){
            instance=new FingerFragment();
        }
        instance.setPerson(person);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        return R.layout.fragment_finger;
    }

    @Override
    protected FingerViewModel initViewModel() {
        return new ViewModelProvider(getActivity(),getViewModelProviderFactory()).get(FingerViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewmodel;
    }

    @Override
    protected void initView() {
        viewModel.mark.setValue(1);
        viewModel.personlive.setValue(person);
        binding.tvSwitch.setOnClickListener(v->{mListener.replaceFragment(FaceFragment.getInstance());});
        viewModel.initFingerResult.observe(this, status -> {
            switch (status) {
                case FAILED:
                    mListener.dismissWaitDialog();
                    Log.e("Fragment_Finger:","Failed");
//                    retryDialog = new MaterialDialog.Builder(getContext())
//                            .title("初始化指纹模块失败，是否重试？")
//                            .positiveText("重试")
//                            .onPositive((dialog, which) -> {
//                                viewModel.initFingerDevice();
//                                dialog.dismiss();
//                            })
//                            .negativeText("退出")
//                            .onNegative((dialog, which) -> {
//                                dialog.dismiss();
//                                mListener.backToStack(HomeFragment.class);
//                            })
//                            .autoDismiss(false)
//                            .show();
                    break;
                case LOADING:
                    mListener.showWaitDialog("正在初始化指纹模块");
                    break;
                case SUCCESS:
                    Log.e("Fragment_Finger:","SUCCESS");
                    mListener.dismissWaitDialog();
//                    viewModel.verifyFinger();
                    viewModel.registerFeature();
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onStart() {
        super.onStart();
//        viewModel.initFingerDevice();
        viewModel.RegFingerDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.releaseFingerDevice();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.releaseFingerDevice();
        EventBus.getDefault().unregister(this);
    }

    private void setPerson(Person person){
        this.person=person;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFingerRegisterEvent(FingerRegisterEvent event) {
        String feature = event.getFeature();
        if (TextUtils.isEmpty(feature)) return;
        if (event.getMark()==1) {
            viewModel.mark.postValue(2);
            viewModel.setFingerFeature1(feature);
        } else if (event.getMark()==2){
            viewModel.mark.postValue(3);
            viewModel.setFingerFeature2(feature);
        }
        Log.e("Finger:",viewModel.personlive.getValue().toString());
        EventBus.getDefault().removeStickyEvent(event);
    }

}
