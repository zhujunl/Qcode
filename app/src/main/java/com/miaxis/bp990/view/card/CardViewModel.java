package com.miaxis.bp990.view.card;

import android.os.SystemClock;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.been.ResultSearch;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.manager.CardManager;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/25 11:19
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CardViewModel extends BaseViewModel {
    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public ObservableField<String> cardMessage = new ObservableField<>();
    public MutableLiveData<ResultSearch> result=new SingleLiveEvent<>();

    private IDCardRecord idCardRecord;
    private Person person;

    public CardViewModel() {
    }

    public void startReadCard() {
        initCardResult.setValue(Status.LOADING);
        CardManager.getInstance().init(App.getInstance(), listener);
    }

    public void stopReadCard() {
        CardManager.getInstance().stopReadCard();
    }

    private CardManager.IDCardListener listener = new CardManager.IDCardListener() {
        @Override
        public void onIDCardInitResult(boolean result) {
            initCardResult.postValue(result ? Status.SUCCESS : Status.FAILED);
            if (result) {
                Log.e("CardManager:","未放身份证");
            }
        }

        @Override
        public void onIDCardReceive(IDCardRecord data, String message) {
            initCardResult.postValue(Status.SUCCESS);
            if (data != null) {
                result.setValue(new ResultSearch(Status.LOADING,null,"正在查询，请稍后.."));
                idCardRecord = data;
                cardMessage.set(message);
                Log.e("CardManager:","读卡成功");
                if (idCardRecord != null) {
                    Disposable sub = Observable.create((ObservableOnSubscribe<Boolean>) p->{
                        String cardnum=getIdCardRecord().getCardNumber();
                        person= PersonManager.getInstance().FindPersonByCard(cardnum);
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
            } else {
                cardMessage.set(message);
            }
        }
    };

    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }
}
