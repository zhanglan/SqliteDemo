package com.zl.demo.sqlite.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BaseDaoFactory {
    private static final String Tag = BaseDaoFactory.class.getSimpleName();
    private String sqliteDatabasePath;

    private SQLiteDatabase db;

    private static BaseDaoFactory instance = null;

    private BaseDaoFactory(){
//        sqliteDatabasePath = "/data/data/com.zl.demo.sqlite/datebases/";
        sqliteDatabasePath = Environment.getExternalStorageDirectory().getPath()+"/inspur";
        Log.d(Tag,"sqliteDatabasePath-->"+sqliteDatabasePath);
        openDatabase();
    }

    public synchronized <T extends BaseDao<M>,M> T getDataHelper(Class<T> clazz,Class<M> entityClass){
        BaseDao baseDao = null;

        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass,db);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    private void openDatabase() {
        File dbFile = new File(sqliteDatabasePath+"/demo.db");
        File logFile = new File(sqliteDatabasePath+"/demo.log");
        if(!dbFile.exists()){
            createDipPath(sqliteDatabasePath+"/demo.db");
        }
        if(!logFile.exists()){
            createDipPath(sqliteDatabasePath+"/demo.log");
        }
        this.db = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath+"/demo.db",null);
    }

    public static BaseDaoFactory getInstance(){
        if(instance==null){
            instance = new BaseDaoFactory();
        }
        return instance;
    }

    private void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
