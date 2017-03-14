package com.zl.demo.sqlite.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.zl.demo.sqlite.db.annotion.DbField;
import com.zl.demo.sqlite.db.annotion.DbTable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    protected static final String Tag = BaseDao.class.getSimpleName();
    private SQLiteDatabase db;
    private boolean isInit = false;
    private Class<T> entityClass;
    private String tableName;

    private HashMap<String,Field> cacheMap;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase db){
        if(!isInit){
            this.db = db;
            entityClass = entity;
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

        return isInit;
    }

    /**
     * 维护表名与成员变量的对应关系
     */
    private void initCacheMap() {
        String sql = "select * from "+this.tableName+" limit 1,0";
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(sql, null);
            String[] columnNames = cursor.getColumnNames();

            Field[] fields = entityClass.getDeclaredFields();
            for(String columnName:columnNames){
                String fieldName = null;
                for(Field field: fields){
                    if(field.getAnnotation(DbField.class)!=null){
                        fieldName = field.getAnnotation(DbField.class).value();
                    }else{
                        fieldName = field.getName();
                    }
                    if(fieldName.equals(columnName)){
                        cacheMap.put(columnName,field);
                        break;
                    }
                }
            }
        }catch (Exception e){
            Log.e(Tag,"Exception:",e);
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    @Override
    public Long insert(T entity) {
        Map<String,String> map = getValues(entity);
        ContentValues  values = getContentValues(map);
        Long result = db.insert(tableName,null,values);
        return result;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if(value != null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    @Override
    public Long update(T entity, T where) {
        return null;
    }

    protected abstract String createTable();

    private Map<String,String> getValues(T entity){
        HashMap<String,String> result = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()){
            Field columnField = fieldIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if(columnField.getAnnotation(DbField.class)!=null){
                cacheKey = columnField.getAnnotation(DbField.class).value();
            }else{
                cacheKey = columnField.getName();
            }

            try {
                if(null==columnField.get(entity)){
                    continue;
                }
                cacheValue = columnField.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }
        return result;
    }
}
