package com.zl.demo.sqlite.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BaseDaoFactory {

    private String sqliteDatabasePath;

    private SQLiteDatabase db;

    private static BaseDaoFactory instance = new BaseDaoFactory();

    private BaseDaoFactory(){
        sqliteDatabasePath = Environment.getDataDirectory().getAbsolutePath()+"demo.db";
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
        this.db = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    }

    public static BaseDaoFactory getInstance(){
        return instance;
    }

}
