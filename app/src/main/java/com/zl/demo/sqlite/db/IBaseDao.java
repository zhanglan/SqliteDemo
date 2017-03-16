package com.zl.demo.sqlite.db;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public interface IBaseDao<T> {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    Long insert(T entity);

    /**
     * 修改数据
     * @param entity
     * @param where
     * @return
     */
    int update(T entity,T where);

    /**
     * 修改数据（支持复杂条件，如where name in ('1','2')）
     * @param entity
     * @param where
     * @return
     */
    int update(T entity,String where);

    /**
     * 删除数据
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 删除数据（支持复杂条件）
     * @param where
     * @return
     */
    int delete(String where);

    /**
     * 查询数据
     * @param where
     * @return
     */
    List<T> query(T where);

    /**
     * 查询数据（增加了排序，分页）
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    /**
     * 查询数据（更复杂的查询，直接用sql）
     * @param sql
     * @return
     */
    List<T> query(String sql);
}
