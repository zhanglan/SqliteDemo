package com.zl.demo.sqlite;

import com.zl.demo.sqlite.db.annotion.DbField;
import com.zl.demo.sqlite.db.annotion.DbTable;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@DbTable("tb_common_user")
public class User {
    @DbField("tb_name")
    public String name;
    @DbField("tb_password")
    public String password;

}
