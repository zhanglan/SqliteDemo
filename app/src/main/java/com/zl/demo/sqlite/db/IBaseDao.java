package com.zl.demo.sqlite.db;

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
    Long update(T entity,T where);
}
