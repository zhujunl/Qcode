package com.miaxis.phone;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZJL
 * @date 2022/3/24 15:51
 * @des
 * @updateAuthor
 * @updateDes
 */
public class App extends Application {
    private static App app;
    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }

    public static App getInstance(){
        return app;
    }

    public ExecutorService getThreadExecutor(){
        return threadExecutor;
    }
}
