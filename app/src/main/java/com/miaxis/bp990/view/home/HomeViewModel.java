package com.miaxis.bp990.view.home;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.ResultSearch;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.util.IDCardUtils;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/24 18:14
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HomeViewModel extends BaseViewModel {
    public MutableLiveData<ResultSearch> result=new SingleLiveEvent<>();

    public Person person;

    public void SearchPerson(String id){
        String tip= IDCardUtils.IDCardValidate(id);
        if(tip.equals("0")){
            Disposable sub = Observable.create((ObservableOnSubscribe<Boolean>) p->{
                person= PersonManager.getInstance().FindPersonByCard(id);
                p.onNext(Boolean.TRUE);
            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(flag->{
                        result.setValue(new ResultSearch(Status.SUCCESS,person,""));
//                        if(person!=null){
//                            mListener.replaceFragment(FaceFragment.getInstance(person));
//                        }else {
//                            ToastManager.toast("查无此人",ToastManager.ERROR);
//                        }
                    },throwable -> {
                        result.setValue(new ResultSearch(Status.FAILED,null,throwable.getMessage()));
                    });
        }else {
            ToastManager.toast(tip,ToastManager.ERROR);
        }
    }
}
