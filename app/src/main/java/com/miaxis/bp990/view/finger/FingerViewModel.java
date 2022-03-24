package com.miaxis.bp990.view.finger;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.event.FingerRegisterEvent;
import com.miaxis.bp990.manager.FingerManager;

import org.greenrobot.eventbus.EventBus;

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
    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();

    public MutableLiveData<Integer> mark=new MutableLiveData<>();
    public MutableLiveData<Boolean> fingerImageUpdate = new SingleLiveEvent<>();
    public ObservableField<String> status = new ObservableField<>();
    public MutableLiveData<Person> personlive=new MutableLiveData<>();
    private int progress = 1;

    public Bitmap fingerImageCache;

    private byte[] feature1;
    private byte[] feature2;
    private byte[] feature3;
    private String fingerFeature1;
    private String fingerFeature2;

    public byte[] template;


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
        }else {
            Log.e("Finger:","请按手指");
        }
    }

    public void RegFingerDevice() {
        initFingerResult.setValue(Status.LOADING);
        FingerManager.getInstance().init(App.getInstance(), Reglistener);
    }

    public void registerFeature() {
        progress = 1;
        status.set("请按手指（0/3）");
        FingerManager.getInstance().getFingerFeatureAndImage();
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
            IDCardRecord idCardRecord = idCardRecordLiveData.getValue();
//            if (idCardRecord == null) return;
//            if (TextUtils.isEmpty(idCardRecord.getFingerprint0()) || TextUtils.isEmpty(idCardRecord.getFingerprint1())) {
//                return;
//            }
            Log.e("FingerManager:","null");
            if (feature != null) {
                List<String> featureList = new ArrayList<>();
                featureList.add(idCardRecord.getFingerprint0());
                featureList.add(idCardRecord.getFingerprint1());
                for (String value : featureList) {
                    boolean result = false;
                    try {
                        result = FingerManager.getInstance().matchFeature(feature, Base64.decode(value, Base64.NO_WRAP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        Log.e("Finger:","比对成功");
                        fingerResultFlag.postValue(Boolean.TRUE);
                        return;
                    }
                }
                fingerResultFlag.postValue(Boolean.FALSE);

                Log.e("Finger:","比对失败，请重按手指");
            }
            FingerManager.getInstance().getFingerFeature();
        }
    };


    private FingerManager.FingerListener Reglistener = new FingerManager.FingerListener() {
        @Override
        public void onFingerInitResult(boolean result) {
            initFingerResult.postValue(result ? Status.SUCCESS : Status.FAILED);
        }

        @Override
        public void onFingerReceive(byte[] feature, Bitmap image, boolean hasImage) {
            if (feature == null) {
                FingerManager.getInstance().getFingerFeatureAndImage();
                return;
            }
            Log.e("Finger:","feature:"+feature);
            if (hasImage) {
                fingerImageCache = image;
                fingerImageUpdate.postValue(true);
            }
            if (progress == 1) {
                feature1 = feature;
                status.set("请按手指（1/3）");
                progress = 2;
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 2) {
                feature2 = feature;
                status.set("请按手指（2/3）");
                progress = 3;
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 3) {
                feature3 = feature;
                if (feature1 != null && feature2 != null) {
                    template = FingerManager.getInstance().mergeFeature(feature1, feature2, feature3);
                    if (template != null) {
                        status.set("指纹采集成功，请验证手指");
                        progress = 4;
                        FingerManager.getInstance().getFingerFeatureAndImage();
                        return;
                    }
                }
                progress = 1;
                status.set("指纹模板合成失败，请重新采集\n请按手指（0/3）");
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 4) {
                if (template != null) {
                    boolean result = FingerManager.getInstance().matchFeature(template, feature);
                    if (result) {
                        status.set("指纹采集成功");
                        EventBus.getDefault().postSticky(new FingerRegisterEvent(mark.getValue(), Base64.encodeToString(template, Base64.NO_WRAP)));
                        fingerResultFlag.postValue(Boolean.TRUE);
                    } else {
                        status.set("验证失败，请重新验证手指");
                        FingerManager.getInstance().getFingerFeatureAndImage();
                    }
                } else {
                    progress = 1;
                    status.set("指纹模板合成失败，请重新采集\n请按手指（0/3）");
                    FingerManager.getInstance().getFingerFeatureAndImage();
                }
            }
        }
    };

    public void setFingerFeature1(String fingerFeature1) {
        personlive.getValue().setFinger1(fingerFeature1);
        Log.e("fingerFeature1","1111");
    }

    public void setFingerFeature2(String fingerFeature2) {
        personlive.getValue().setFinger2(fingerFeature2);
        Log.e("fingerFeature2","222222");
    }

}
