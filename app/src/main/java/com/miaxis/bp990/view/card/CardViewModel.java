package com.miaxis.bp990.view.card;

import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.manager.CardManager;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

/**
 * @author ZJL
 * @date 2022/3/25 11:19
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CardViewModel extends BaseViewModel {
    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> readCardFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public ObservableField<String> cardMessage = new ObservableField<>();

    private IDCardRecord idCardRecord;

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
                idCardRecord = data;
                cardMessage.set(message);
                Log.e("CardManager:","读卡成功");
                if (idCardRecord != null) {
                    readCardFlag.setValue(Boolean.TRUE);
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
