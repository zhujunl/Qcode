package com.miaxis.bp990.manager;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.manager.idpower.BP990_IdCardPower;
import com.miaxis.bp990.manager.idpower.BP990s_IdCardPower;
import com.miaxis.bp990.manager.idpower.IIdCardPower;
import com.miaxis.bp990.util.ValueUtil;
import com.zz.impl.IDCardDeviceImpl;
import com.zz.impl.IDCardInterfaceService;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;

public class CardManager {

    private CardManager() {
    }

    public static CardManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CardManager instance = new CardManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private static final int BAUD_RATE = 115200;


    private IDCardInterfaceService cardManager;

    private Context context;
    private static IDCardListener listener;

    static  Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (listener!=null) {
                if (msg.what == 0) {
                    listener.onIDCardReceive((IDCardRecord) msg.obj, "读卡成功");
                } else if (msg.what == 1) {
                    listener.onIDCardReceive(null, (String) msg.obj);
                }
            }
        }
    };
    private volatile AtomicBoolean running = new AtomicBoolean(false);
    IIdCardPower mPowerManager = null;


    public void init(@NonNull Context context,  IDCardListener listener) {
        this.context = context;
        CardManager.listener = listener;
//        Log.e("asd", "MANUFACTURER：" + Build.MANUFACTURER);
        if (Objects.equals(Build.MANUFACTURER, "QUALCOMM")) {
            mPowerManager = new BP990_IdCardPower();
        } else {
            mPowerManager = new BP990s_IdCardPower();
        }
        initDevice();
    }


    private void initDevice() {
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                mPowerManager.powerOn();
                cardManager = new IDCardDeviceImpl();
                listener.onIDCardInitResult(isDeviceOpen());
                startReadCard();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onIDCardInitResult(false);
            }
        });
    }

    private boolean isDeviceOpen() throws InterruptedException {
        int count = 4;
        String samId = readSamId();
        //Log.e("asd", "ssssssssss" + samId);
        while (TextUtils.isEmpty(samId)) {
            Thread.sleep(100);
            samId = readSamId();
            count--;
            //Log.e("asd", "sadadsadsadsa" + count);
            if (!TextUtils.isEmpty(samId)) {
                return true;
            }
            if (count == 0) {
                return false;
            }
        }
        return true;
    }

    private void startReadCard() {
        running.set(true);
        new ReadCardThread().start();
    }

    public void stopReadCard() {
        running.set(false);
        cardManager = null;
        mPowerManager.powerOff();
    }

    private class ReadCardThread extends Thread {

        @Override
        public void run() {
            while (running.get()) {
                if (cardManager != null) {
                    byte[] message = new byte[100];
                    try {
                        int result = cardManager.readIDCard(mPowerManager.ioPath(), BAUD_RATE, 10, message);
                        if (result == 0x90) {
                            IDCardRecord transform;
                            int cardType = cardManager.getIDCardType();
                            if (cardType == 0 || cardType == 1 || cardType == 2) {
                                if (cardType == 0) {
                                    transform = transformID(cardManager);
                                } else if (cardType == 1) {
                                    transform = transformGreen(cardManager);
                                } else {
                                    transform = transformGAT(cardManager);
                                }
                                transformFingerprint(cardManager, transform);
                                if (listener != null&&handler!=null) {
                                    stopReadCard();
                                    handler.sendMessage(handler.obtainMessage(0, transform));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("asd", "读卡异常" + new String(message));
                    }
                } else {
                    stopReadCard();
                    break;
                }
            }
        }
    }

    private IDCardRecord transformID(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("")
                .name(cardManager.getName())
                .cardNumber(cardManager.getIdNumber())
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private IDCardRecord transformGreen(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("I")
                .name(cardManager.getEnglishName())
                .cardNumber(cardManager.getIdNumber())
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private IDCardRecord transformGAT(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("J")
                .name(cardManager.getName())
                .cardNumber(cardManager.getIdNumber())
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private void transformFingerprint(IDCardInterfaceService cardManager, IDCardRecord idCardRecord) {
        byte[] fingerData = new byte[1024];
        int i = cardManager.getFingerData(fingerData);
        if (i == 0) {
            byte[] bFingerData0 = new byte[512];
            byte[] bFingerData1 = new byte[512];
            System.arraycopy(fingerData, 0, bFingerData0, 0, bFingerData0.length);
            System.arraycopy(fingerData, 512, bFingerData1, 0, bFingerData1.length);
            if (bFingerData0[5]!=0){
                idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
                idCardRecord.setFingerprintPosition0(ValueUtil.fingerPositionCovert(bFingerData0[5]));
            }
            if (bFingerData1[5]!=0){
                idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
                idCardRecord.setFingerprintPosition1(ValueUtil.fingerPositionCovert(bFingerData1[5]));
            }
        }
    }

    /**
     * 检查身份证是否已经过期
     *
     * @return true - 已过期 false - 未过期
     */
//    public boolean checkIsOutValidate(IDCardRecord idCardRecord) {
//        try {
//            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//            Date validEndDate = myFmt.parse(idCardRecord.getValidateEnd());
//            return validEndDate.getTime() < System.currentTimeMillis();
//        } catch (ParseException e) {
//            return false;
//        }
//    }

    private String readSamId() {
        try {
            byte[] message = new byte[201];
            byte[] samVersion = new byte[201];
            int result = cardManager.getSAMID(mPowerManager.ioPath(), BAUD_RATE, samVersion, message);
            if (result == 0x90) {
                return new String(samVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface IDCardListener {
        void onIDCardInitResult(boolean result);

        void onIDCardReceive(IDCardRecord idCardRecord, String message);
    }


}
