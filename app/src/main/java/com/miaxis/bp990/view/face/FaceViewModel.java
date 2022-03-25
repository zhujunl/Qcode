package com.miaxis.bp990.view.face;

import android.graphics.Bitmap;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.PhotoFaceFeature;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.exception.MyException;
import com.miaxis.bp990.manager.CameraManager;
import com.miaxis.bp990.manager.FaceManager;
import com.miaxis.bp990.util.ValueUtil;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/3/24 18:28
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FaceViewModel extends BaseViewModel {

    public ObservableField<String> hint = new ObservableField<>("");
    public MutableLiveData<Boolean> verifyFailedFlag = new SingleLiveEvent<>();
    public MutableLiveData<Person> personlive=new MutableLiveData<>();

    public MutableLiveData<String> path=new MutableLiveData<>();

    private PhotoFaceFeature cardFeature;

    public FaceViewModel() {
    }

    public void startFaceVerify(Bitmap face) {
        cardFeature=null;
        hint.set("身份证证件照处理中");
        Disposable sub=Observable.create((ObservableOnSubscribe<PhotoFaceFeature>) p->{
            PhotoFaceFeature photoFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(face);
            p.onNext(photoFaceFeature);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoFaceFeature->{
                    if(photoFaceFeature==null){
                        hint.set("出现错误");
                        return;
                    }
                    cardFeature=photoFaceFeature;
                    FaceManager.getInstance().setFeatureListener(faceListener);
                    FaceManager.getInstance().setNeedNextFeature(true);
                    FaceManager.getInstance().setOrientation(CameraManager.getInstance().getPreviewOrientation());
                    FaceManager.getInstance().startLoop();
                    hint.set("请将镜头朝向查询的人员");
                },throwable -> {
                    if (throwable instanceof MyException) {
                        hint.set(throwable.getMessage());
                    } else {
                        hint.set("出现错误");
                    }
                });

    }

    public void stopFaceVerify() {
        FaceManager.getInstance().stopLoop();
        FaceManager.getInstance().setFeatureListener(null);
    }

    private FaceManager.OnFeatureExtractListener faceListener = (mxRGBImage, mxFaceInfoEx, feature, mask) -> {
            try {
                float score;
                if (mask) {
                    score = FaceManager.getInstance().matchMaskFeature(feature, cardFeature.getMaskFaceFeature());
                } else {
                    score = FaceManager.getInstance().matchFeature(feature, cardFeature.getFaceFeature());
                }
                Log.e("faceListener:","mask="+mask+"-----------------score="+score);
                int verify;
                if (mask ? score >= ValueUtil.DEFAULT_MASK_VERIFY_SCORE : score >= ValueUtil.DEFAULT_VERIFY_SCORE) {
                    verify = 1;
                    verifyFailedFlag.postValue(Boolean.TRUE);
                    hint.set("人证核验成功");
                } else {
                    verify = 2;
                    Log.e("比对", "比对失败: "+score);
                    verifyFailedFlag.postValue(Boolean.FALSE);
                    hint.set("识别不通过");
                }
                stopFaceVerify();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);

    };



}
