package com.miaxis.bp990.view.finger;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentFingerBinding;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import androidx.lifecycle.Observer;
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
    private MaterialDialog retryDialog;
    private int status;

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

    public static FingerFragment getInstance(Person person,int status){
        if (instance==null){
            instance=new FingerFragment();
        }
        instance.setPerson(person);
        instance.setStatus(status);
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
        viewModel.personlive.setValue(person);
        binding.ivBack.setOnClickListener(v->onBackPressed());
        binding.tvSwitch.setOnClickListener(v->{mListener.replaceFragment(FaceFragment.getInstance(person));});
        binding.title.setText(person.getName());
        viewModel.fingerResultFlag.observe(this, fingerResultFlagObserver);
        viewModel.initFingerResult.observe(this, status -> {
            switch (status) {
                case FAILED:
                    mListener.dismissWaitDialog();
                    Log.e("Fragment_Finger:","Failed");
                    retryDialog = new MaterialDialog.Builder(getContext())
                            .title("初始化指纹模块失败，是否重试？")
                            .positiveText("重试")
                            .onPositive((dialog, which) -> {
                                viewModel.initFingerDevice();
                                dialog.dismiss();
                            })
                            .negativeText("退出")
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                                mListener.backToStack(HomeFragmet.class);
                            })
                            .autoDismiss(false)
                            .show();
                    break;
                case LOADING:
                    mListener.showWaitDialog("正在初始化指纹模块");
                    break;
                case SUCCESS:
                    Log.e("Fragment_Finger:","SUCCESS");
                    mListener.dismissWaitDialog();
                    viewModel.verifyFinger();
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragmet.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.initFingerDevice();
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
    }

    private void setPerson(Person person){
        this.person=person;
    }

    private void setStatus(int status){
        this.status=status;
    }


    private Observer<Boolean> fingerResultFlagObserver = flag ->{
        if (flag){
            showResult();
            Log.e("Finger:","比对成功");
        }else {
            binding.tvHint.setText("指纹核验失败，请重按手指");
            Log.e("Finger:","比对失败，请重按手指");
        }
    };

    private void showResult(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("核验通过")
                .setMessage("健康状态："+(status!=-1?getPersonStauts(status):getPersonStauts(person.getCodestatus())))
                .setCancelable(false)
                .setPositiveButton("确认", (dialog, which) -> {
                    mListener.replaceFragment(HomeFragmet.getInstance());
                    dialog.dismiss();
                });
        AlertDialog alert=builder.create();
        alert.show();
    }

    private String getPersonStauts(int status){
        String s="健康";
        switch (status){
            case 1:
                s="亚健康";
                break;
            case 2:
                s="不健康";
                break;
            default:
                s="健康";
                break;
        }
        return s;
    }
}
