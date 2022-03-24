package com.miaxis.bp990.view.card;

import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.manager.CardManager;

/**
 * @author ZJL
 * @date 2022/3/24 18:09
 * @des
 * @updateAuthor
 * @updateDes
 */
class card {
    private void initCard(){
        CardManager.getInstance().init(App.getInstance(), new CardManager.IDCardListener() {
            @Override
            public void onIDCardInitResult(boolean result) {
                Log.e("TAG","CardManager"+result);
            }

            @Override
            public void onIDCardReceive(IDCardRecord idCardRecord, String message) {
                if (idCardRecord!=null){

                    Log.e("TAG","CardManager"+idCardRecord);
                }
            }
        });
    }
}
