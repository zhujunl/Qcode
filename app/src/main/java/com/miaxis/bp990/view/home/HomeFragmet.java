package com.miaxis.bp990.view.home;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.miaxis.bp990.BuildConfig;
import com.miaxis.bp990.CaptureFragmentActivity;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.databinding.FragmentHomeBinding;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.view.card.CardFragment;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.register.RegisterFragment;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/24 18:13
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HomeFragmet extends BaseViewModelFragment<FragmentHomeBinding,HomeViewModel>{

    private static HomeFragmet instance;
    private OnFragmentInteractionListener mListener;
    int count=0;
    public static HomeFragmet getInstance(){
        return new HomeFragmet();
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
        return R.layout.fragment_home;
    }

    @Override
    protected HomeViewModel initViewModel() {
        return new ViewModelProvider(getActivity(),getViewModelProviderFactory()).get(HomeViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.bp990.BR.viewmodel;
    }

    @Override
    protected void initView() {
        viewModel.result.observe(this, resultSearch -> {
            if(resultSearch.getCode()== Status.SUCCESS){
                if (resultSearch.getPerson()!=null){
                    mListener.replaceFragment(FaceFragment.getInstance(resultSearch.getPerson()));
                }else {
                    ToastManager.toast("查无此人",ToastManager.ERROR);
                }
            }else {
                ToastManager.toast(resultSearch.getMessage(),ToastManager.ERROR);
            }
        });
        binding.card.setOnClickListener(v -> mListener.replaceFragment(CardFragment.getInstance()));
        binding.inputSeach.setOnClickListener(v->{
            showDialog();
        });
        binding.scan.setOnClickListener(v->{
            mListener.checkCamera(CaptureFragmentActivity.class,"网证查询",1);
        });
        binding.hScan.setOnClickListener(v->{
            mListener.checkCamera(CaptureFragmentActivity.class,"健康码查询",2);
        });
        binding.title.setOnClickListener(v->{
            count++;
            if(count==6){
                mListener.replaceFragment(RegisterFragment.getInstance());
                count=0;
            }
        });
        binding.verson.setText("版本号："+ BuildConfig.VERSION_NAME);
    }

    @Override
    public void onBackPressed() {
        mListener.exitApp();
    }

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_login,null);
        EditText name=view.findViewById(R.id.name_et);
        EditText num=view.findViewById(R.id.code_et);
        TextView cancle=view.findViewById(R.id.dia_cancle);
        TextView sure=view.findViewById(R.id.dia_sure);
        EditText editText=new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("请输入身份证号码")
                .setCancelable(true)
                .setView(view);
        AlertDialog alert=builder.create();
        cancle.setOnClickListener(v -> alert.dismiss());
        sure.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name.getText().toString().trim())||TextUtils.isEmpty(num.getText().toString().trim())){
                ToastManager.toast("请填写完整信息",ToastManager.ERROR);
            }else {
                viewModel.SearchPerson(num.getText().toString().trim());
                alert.dismiss();
            }
        });
        alert.show();
    }
}
