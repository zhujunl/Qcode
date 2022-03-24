package com.miaxis.bp990.manager.fingerPower;

import android.os.SystemClock;

import com.miaxis.bp990.manager.CardManager;
import com.miaxis.bp990.manager.GpioManager;


/**
 * BP990_FingerPower
 *
 * @author zhangyw
 * Created on 2021/3/21.
 */
public class BP990_FingerPower implements IFingerPower {

    @Override
    public void powerOn() {
        openDevice();
    }

    @Override
    public void powerOff() {
        closeDevice();
    }

    private void openDevice() {
        synchronized (CardManager.class) {
            //            if (!GpioManager.getInstance().getCardDevicePowerStatus()) {
            GpioManager.getInstance().fingerDevicePowerControl(true);
            SystemClock.sleep(500);
            //            }
        }
    }

    private void closeDevice() {
        synchronized (CardManager.class) {
            //            if (GpioManager.getInstance().getCardDevicePowerStatus()) {
            GpioManager.getInstance().fingerDevicePowerControl(false);
            SystemClock.sleep(200);
            //            }
        }
    }

}
