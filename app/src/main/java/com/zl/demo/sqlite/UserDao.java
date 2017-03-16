package com.zl.demo.sqlite;

import com.zl.demo.sqlite.db.BaseDao;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class UserDao extends BaseDao {
    @Override
    protected String createTable() {
        return "create table if not exists tb_user(name integer primary key,password text)";
    }
}
