package com.miaxis.bp990.manager.idpower;

import android.os.SystemClock;

import com.miaxis.bp990.manager.CardManager;
import com.miaxis.bp990.manager.GpioManager;


/**
 * BP990_IdCardPower
 *
 * @author zhangyw
 * Created on 2021/3/21.
 */
public class BP990_IdCardPower implements IIdCardPower {
    private static final String SERIAL_PORT = "/dev/ttyHSL2";
    @Override
    public void powerOn() {
        openDevice();
    }

    @Override
    public void powerOff() {
        closeDevice();
    }

    @Override
    public String ioPath() {
        return SERIAL_PORT;
    }

    private void openDevice() {
        synchronized (CardManager.class) {
            if (!GpioManager.getInstance().getCardDevicePowerStatus()) {
                GpioManager.getInstance().fingerDevicePowerControl(true);
                GpioManager.getInstance().cardDevicePowerControl(true);
                SystemClock.sleep(500);
            }

        }
    }

    private void closeDevice() {
        synchronized (CardManager.class) {
            if (GpioManager.getInstance().getCardDevicePowerStatus()) {
                GpioManager.getInstance().cardDevicePowerControl(false);
                SystemClock.sleep(200);
            }
        }
    }

}
