package com.zl.demo.sqlite.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zl.demo.sqlite.db.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase db;
    private boolean isInit = false;
    private Class<T> entityClass;
    private String tableName;

    private HashMap<String,Field> cacheMap;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase db){
        if(!isInit){
            this.db = db;
            if(entity.getAnnotation(DbTable.class)==null){
                tableName = entity.getClass().getSimpleName();
            }else{
                tableName = entity.getAnnotation(DbTable.class).value();
            }
            if(!db.isOpen()){
                return false;
            }
            if(!TextUtils.isEmpty(createTable())){
                db.execSQL(createTable());
            }
            initCacheMap();
            isInit = true;
        }

        return true;
    }

    private void initCacheMap() {
        String sql = "select * from "+this.tableName+" limit 1,0";
        Cursor cursor = null;
        cursor = db.rawQuery(sql, null);
        String[] columnName = cursor.getColumnNames();
    }

    @Override
    public Long insert(T entity) {
        return null;
    }

    @Override
    public Long update(T entity, T where) {
        return null;
    }

    protected abstract String createTable();
}
