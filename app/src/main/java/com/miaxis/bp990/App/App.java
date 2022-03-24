package com.miaxis.bp990.App;

import android.app.Application;

import com.miaxis.bp990.data.dao.AppDatabase;
import com.miaxis.bp990.data.entity.ConfigManager;
import com.miaxis.bp990.manager.FaceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZJL
 * @date 2022/3/24 15:33
 * @des
 * @updateAuthor
 * @updateDes
 */
public class App extends Application{
    private static App app;
    private ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }

    public void initApplication(){
        int result = FaceManager.getInstance().initFaceST(getApplicationContext(), "");
        AppDatabase.initDB(this);
        ConfigManager.getInstance().checkConfig();
    }

    public static App getInstance(){
        return app;
    }

    public ExecutorService getThreadExecutor(){
        return threadExecutor;
    }
}
