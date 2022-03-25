package com.miaxis.bp990.view.home;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.BR;
import com.miaxis.bp990.BuildConfig;
import com.miaxis.bp990.CaptureFragmentActivity;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.databinding.FragmentHomeBinding;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.util.IDCardUtils;
import com.miaxis.bp990.view.card.CardFragment;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.register.RegisterFragment;

import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        return BR.viewmodel;
    }

    @Override
    protected void initView() {
        binding.card.setOnClickListener(v -> mListener.replaceFragment(CardFragment.getInstance()));
        binding.inputSeach.setOnClickListener(v->{
            showDialog();
        });
        binding.scan.setOnClickListener(v->{
            mListener.checkCamera(CaptureFragmentActivity.class,"扫码查询");
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
        mListener.backToStack(null);
    }

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        EditText editText=new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("请输入身份证号码")
                .setCancelable(true)
                .setView(editText)
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    String id=editText.getText().toString().trim();
                    if(!TextUtils.isEmpty(id)){
                        String tip=IDCardUtils.IDCardValidate(id);
                        if(tip.equals("0")){
                            Disposable sub =Observable.create((ObservableOnSubscribe<Person>) p->{
                                Person person= PersonManager.getInstance().FindPersonByCard(id);
                                p.onNext(person);
                            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(person->{
                                        if(person!=null){
                                            mListener.replaceFragment(FaceFragment.getInstance(person));
                                        }else {
                                            ToastManager.toast("查无此人",ToastManager.ERROR);
                                        }
                                    },throwable -> {
                                        ToastManager.toast(throwable.getMessage(),ToastManager.ERROR);
                                    });
                        }else {
                            ToastManager.toast(tip,ToastManager.ERROR);
                        }
                    }else {
                        ToastManager.toast("请输入身份证号码",ToastManager.ERROR);
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
