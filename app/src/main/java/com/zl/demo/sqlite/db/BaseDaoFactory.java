package com.zl.demo.sqlite.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BaseDaoFactory {
    private static final String Tag = BaseDaoFactory.class.getSimpleName();
    private String sqliteDatabasePath;

    private SQLiteDatabase db;

    private static BaseDaoFactory instance = null;

    private BaseDaoFactory(){
        sqliteDatabasePath = "/data/data/com.zl.demo.sqlite/datebases/";
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
        File dir = new File(sqliteDatabasePath);
        if(!dir.exists()){
           dir.mkdirs();
        }
        File dbFile = new File(sqliteDatabasePath+"demo.db");
        if(!dbFile.exists()){
            Log.d(Tag,"1111");
        }
        this.db = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath+"demo.db",null);
    }

    public static BaseDaoFactory getInstance(){
        if(instance==null){
            instance = new BaseDaoFactory();
        }
        return instance;
    }

}
