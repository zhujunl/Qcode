package com.miaxis.bp990.view.card;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.bp990.App.App;
import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.databinding.FragmentCardBinding;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/24 18:09
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CardFragment extends BaseViewModelFragment<FragmentCardBinding,CardViewModel> {

    private static CardFragment instance;
    private OnFragmentInteractionListener mListener;
    private MaterialDialog retryDialog;

    public static CardFragment getInstance(){
        if(instance==null){
            instance=new CardFragment();
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
        return R.layout.fragment_card;
    }

    @Override
    protected CardViewModel initViewModel() {
        return new ViewModelProvider(this,getViewModelProviderFactory()).get(CardViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewmodel;
    }

    @Override
    protected void initData() {
        viewModel.initCardResult.observe(this, initCardResultObserver);
        viewModel.readCardFlag.observe(this, readCardFlagObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
    }


    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (retryDialog != null) {
            retryDialog.dismiss();
        }
        mListener.backToStack(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.startReadCard();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.stopReadCard();
    }

    private Observer<Status> initCardResultObserver = status -> {
        switch (status) {
            case FAILED:
                mListener.dismissWaitDialog();
                retryDialog = new MaterialDialog.Builder(getContext())
                        .title("初始化身份证阅读器失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.startReadCard();
                            dialog.dismiss();
                        })
                        .negativeText("退出")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            onBackPressed();
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化身份证阅读器");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                break;
        }
    };

    private Observer<Boolean> readCardFlagObserver = flag -> {
        Disposable sub =Observable.create((ObservableOnSubscribe<Person>) p->{
            String cardnum=viewModel.getIdCardRecord().getCardNumber();
            Person person=PersonManager.getInstance().FindPersonByCard(cardnum);
            p.onNext(person);
        } ).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(person -> {
                    if (person!=null) {
                        mListener.replaceFragment(FaceFragment.getInstance(person));
                    }else {
                        ToastManager.toast("暂无",ToastManager.ERROR);
                    }
                }, throwable -> {
                    ToastManager.toast("暂无",ToastManager.ERROR);
                });


    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragmet.class);

    private View.OnLongClickListener alarmListener = v -> {
        //viewModel.alarm();
        return false;
    };

}
