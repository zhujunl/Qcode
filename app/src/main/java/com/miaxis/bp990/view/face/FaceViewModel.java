package com.miaxis.bp990.view.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseViewModel;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.been.PhotoFaceFeature;
import com.miaxis.bp990.bridge.SingleLiveEvent;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.manager.CameraManager;
import com.miaxis.bp990.manager.FaceManager;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.util.FileUtil;
import com.miaxis.bp990.util.ValueUtil;

import java.io.File;

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

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public MutableLiveData<IDCardRecord> verifyFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> verifyFailedFlag = new SingleLiveEvent<>();
    public MutableLiveData<Person> personlive=new MutableLiveData<>();

    public MutableLiveData<String> path=new MutableLiveData<>();

    private PhotoFaceFeature cardFeature;

    public FaceViewModel() {
    }

    public void startFaceVerify(String card,IDCardRecord idCardRecord) {
        cardFeature=null;
        Disposable sub=Observable.create((ObservableOnSubscribe<PhotoFaceFeature>) p->{
            Person person= PersonManager.getInstance().FindPersonByCard(card);
            PhotoFaceFeature photoFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(idCardRecord.getCardBitmap());
            p.onNext(photoFaceFeature);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoFaceFeature->{
                    cardFeature=photoFaceFeature;
                    FaceManager.getInstance().setFeatureListener(faceListener);
                    FaceManager.getInstance().setNeedNextFeature(true);
                    FaceManager.getInstance().setOrientation(CameraManager.getInstance().getPreviewOrientation());
                    FaceManager.getInstance().startLoop();
                },throwable -> {
                    Log.e("FaceViewModel:",throwable.getMessage());
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
                int verify;

                if (mask ? score >= ValueUtil.DEFAULT_MASK_VERIFY_SCORE : score >= ValueUtil.DEFAULT_VERIFY_SCORE) {
                    verify = 1;


                    verifyFailedFlag.postValue(Boolean.TRUE);
                } else {
                    verify = 2;
                    Log.e("比对", "比对失败: "+score);

                    verifyFailedFlag.postValue(Boolean.FALSE);
                }
                stopFaceVerify();
                byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                Bitmap header = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                IDCardRecord value = idCardRecordLiveData.getValue();
                if (value != null) {
//                    IDCardRepository.getInstance().addNewIDCard(value);
//                    value.setFaceBitmap(header);
//                    value.setVerifyTime(new Date());
//                    value.setChekStatus(verify);
//                    verifyFlag.postValue(value);
//                    PostalManager.getInstance().saveImage(header,value,readCardNum);
                    return;
                } else {
                    toast.postValue(ToastManager.getToastBody("遇到错误，请退出后重试", ToastManager.ERROR));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);

    };

    public void SavePic(Person person,Bitmap data){
        String path=FileUtil.PICTURE+ File.separator+System.currentTimeMillis()+".ss";
        FileUtil.saveBitmapToJPEG(data,path);
        person.setFacepath(path);
        personlive.setValue(person);
        this.path.setValue(path);
    }

}
