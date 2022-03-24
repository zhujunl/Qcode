package com.miaxis.bp990.data.entity;


import com.miaxis.bp990.data.dao.AppDatabase;

public class ConfigModel {

    public static void saveConfig(Config config) {
        config.setId(1L);
        AppDatabase.getInstance().configdao().deleteAll();
        AppDatabase.getInstance().configdao().insert(config);
    }

    public static Config loadConfig() {
        Config config = AppDatabase.getInstance().configdao().loadConfig();
        return config;
    }

}
