package com.miaxis.bp990.view.finger;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.manager.FingerManager;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

/**
 * @author ZJL
 * @date 2022/3/24 19:24
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FingerViewModel extends BaseViewModel {

    public MutableLiveData<Status> initFingerResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();
    public ObservableField<String> hint = new ObservableField<>("");
    public MutableLiveData<Person> personlive=new MutableLiveData<>();



    public FingerViewModel() {
    }

    public void initFingerDevice() {
        initFingerResult.setValue(Status.LOADING);
        FingerManager.getInstance().init(App.getInstance(), listener);
    }

    public void verifyFinger() {
        int fingerFeature = FingerManager.getInstance().getFingerFeature();
        if (fingerFeature!=0){
            Log.e("Finger:","请先初始化");
            hint.set("请先初始化");
        }else {
            Log.e("Finger:","请按手指");
            hint.set("请按手指");
        }
    }


    public void releaseFingerDevice() {
        FingerManager.getInstance().release();
    }


    private FingerManager.FingerListener listener = new FingerManager.FingerListener() {
        @Override
        public void onFingerInitResult(boolean result) {
            initFingerResult.postValue(result ? Status.SUCCESS : Status.FAILED);
        }

        @Override
        public void onFingerReceive(byte[] feature, Bitmap image, boolean hasImage) {
            Person person=personlive.getValue();
            if (person == null) return;
            if (TextUtils.isEmpty(person.getFinger1()) || TextUtils.isEmpty(person.getFinger1())) {
                return;
            }
            Log.e("FingerManager:","null");
            if (feature != null) {
                List<String> featureList = new ArrayList<>();
                featureList.add(person.getFinger1());
                featureList.add(person.getFinger2());
                for (String value : featureList) {
                    boolean result = false;
                    try {
                        result = FingerManager.getInstance().matchFeature(feature, Base64.decode(value, Base64.NO_WRAP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result) {

                        hint.set("比对成功");
                        fingerResultFlag.postValue(Boolean.TRUE);
                        return;
                    }
                }
                fingerResultFlag.postValue(Boolean.FALSE);
                hint.set("比对失败，请重按手指");
            }
            FingerManager.getInstance().getFingerFeature();
        }
    };



}
