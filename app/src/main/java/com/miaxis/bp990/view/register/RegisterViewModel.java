package com.miaxis.bp990.view.register;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.util.FileUtil;

import java.io.File;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/25 9:27
 * @des
 * @updateAuthor
 * @updateDes
 */
public class RegisterViewModel extends BaseViewModel {


    public final static String FINGER1 = "1";
    public final static String FINGER2 = "2";

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> number = new ObservableField<>();
    public ObservableField<Integer> heath = new ObservableField<>();

    public ObservableField<String> faceFeatureHint = new ObservableField<>("点击采集");


    public MutableLiveData<Boolean> registerFlag = new SingleLiveEvent<>();

    private String featureCache;
    private String maskFeatureCache;
    private Bitmap headerCache;


    public RegisterViewModel() {
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(name.get())
                || TextUtils.isEmpty(number.get())
                || TextUtils.isEmpty(featureCache)
                || TextUtils.isEmpty(maskFeatureCache)
                || headerCache == null) {
            return false;
        }
        return true;
    }

    public void getCourierByPhone() {
        waitMessage.setValue("注册中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
           Person person=new Person.Builder()
                   .name(name.get())
                   .cardnum(number.get())
                   .facepath(featureCache)
                   .codestatus(heath.get())
                   .build();
            PersonManager.getInstance().Save(person);
            SystemClock.sleep(1000);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("注册成功");
                    registerFlag.setValue(Boolean.TRUE);
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(handleError(throwable));
                });
    }

    public void setFeatureCache(String featureCache,Bitmap data) {
        String path= FileUtil.PICTURE+ File.separator+System.currentTimeMillis()+".ss";
        FileUtil.saveBitmapToJPEG(data,path);
        this.featureCache = path;
    }

    public void setMaskFeatureCache(String maskFeatureCache) {
        this.maskFeatureCache = maskFeatureCache;
    }

    public void setHeaderCache(Bitmap headerCache) {
        this.headerCache = headerCache;
    }
}

