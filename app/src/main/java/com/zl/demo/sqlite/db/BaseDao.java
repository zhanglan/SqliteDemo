package com.zl.demo.sqlite.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.zl.demo.sqlite.db.annotion.DbField;
import com.zl.demo.sqlite.db.annotion.DbTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
        cacheMap = new HashMap<>();
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

    @Override
    public int update(T entity, T where) {
        ContentValues values = getContentValues(getValues(entity));
        Map<String,String> whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        int result = db.update(tableName,values,condition.getWhereClause(),condition.getWhereArgs());
        return result;
    }

    @Override
    public int update(T entity, String where) {
        ContentValues values = getContentValues(getValues(entity));
        int result = db.update(tableName,values,where,null);
        return result;
    }

    @Override
    public int delete(T where) {
        Map<String,String> whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        int result = db.delete(tableName,condition.getWhereClause(),condition.getWhereArgs());
        return result;
    }

    @Override
    public int delete(String where) {
        int result = db.delete(tableName,where,null);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        String limitString = null;
        if(startIndex!=null && limit!=null){
            limitString = startIndex + " , " +limit;
        }

        Cursor cursor = db.query(tableName,null,condition.getWhereClause(),condition.getWhereArgs(),null,null,orderBy,limitString);
        List<T> result = getResult(cursor);
        return result;
    }

    @Override
    public List<T> query(String sql) {
        Cursor cursor = db.rawQuery(sql,null);
        List<T> result = getResult(cursor);
        return result;
    }

    @Override
    public String rawQuery(String sql) {
        Cursor cursor = db.rawQuery(sql,null);
        String res = getRawResult(cursor);
        return res;
    }

    private String getRawResult(Cursor cursor){
        JSONArray array = new JSONArray();
        try{
            while (cursor.moveToNext()){
                JSONObject jo = new JSONObject();
                int columnCount = cursor.getColumnCount();
                for(int i=0;i<columnCount;i++){
                    int type = cursor.getType(i);
                    switch (type){
                        case Cursor.FIELD_TYPE_STRING:
                            jo.put(cursor.getColumnName(i),cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            jo.put(cursor.getColumnName(i),cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            jo.put(cursor.getColumnName(i),cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            jo.put(cursor.getColumnName(i),"");
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            jo.put(cursor.getColumnName(i),"");
                            break;
                    }
                }
                array.put(jo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return array.toString();
    }

    private List<T> getResult(Cursor cursor) {
        List<T> list = new ArrayList<>();
        try{
            while (cursor.moveToNext()){
                T item = (T) entityClass.newInstance();
                Iterator iterator = cacheMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String columnName = (String) entry.getKey();
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    if(columnIndex<0) continue;
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();
                    if(type==String.class){
                        field.set(item,cursor.getString(columnIndex));
                    }else if(type == Integer.class){
                        field.set(item,cursor.getInt(columnIndex));
                    }else if(type == Double.class){
                        field.set(item,cursor.getDouble(columnIndex));
                    }else if(type == byte[].class){
                        field.set(item,cursor.getBlob(columnIndex));
                    }else{
                        continue;
                    }
                }
                list.add(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return list;
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

    class Condition{
        private String whereClause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereMap) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" 1=1 ");
            Set<String> kes = whereMap.keySet();
            Iterator<String> iterator = kes.iterator();
            ArrayList<String> list = new ArrayList<>();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = whereMap.get(key);
                if(null!=value){
                    stringBuilder.append(" and "+key+"=? ");
                    list.add(value);
                }
            }
            this.whereClause = stringBuilder.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }
}
