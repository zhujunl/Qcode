package com.miaxis.common.widget.countdown;

/**
 * @author Tank
 * @date 2020/9/10 17:13
 * @des
 * @updateAuthor
 * @updateDes
 */
public interface CountDownListener {

    void onCountDownProgress(int progress);

    void onCountDownStop();
}
