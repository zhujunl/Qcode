package com.miaxis.bp990;

import android.os.SystemClock;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.Code;
import com.miaxis.bp990.been.ResultSearch;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/26 14:09
 * @des
 * @updateAuthor
 * @updateDes
 */
public class MainViewModel extends BaseViewModel {
    public MutableLiveData<ResultSearch> result=new SingleLiveEvent<>();
    private Person person;

    public void searchPerson(Code code){
        Disposable sub= Observable.create((ObservableOnSubscribe<Boolean>) p->{
            person= PersonManager.getInstance().FindPersonByCard(code.cardNumber);
            if(person!=null){
                person.setName(code.name);
                person.setCodestatus(code.codeStatus);
            }
            SystemClock.sleep(1000);
            p.onNext(Boolean.TRUE);
        } ).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flag -> {
                    result.setValue(new ResultSearch(Status.SUCCESS,person,""));
                }, throwable -> {
                    result.setValue(new ResultSearch(Status.FAILED,null,throwable.getMessage()));
                });
    }
}
