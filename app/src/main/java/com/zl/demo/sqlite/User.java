package com.zl.demo.sqlite;

import com.zl.demo.sqlite.db.annotion.DbField;
import com.zl.demo.sqlite.db.annotion.DbTable;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@DbTable("tb_user")
public class User {

    public User(){

    }

    public User(String name,String password){
        this.name = name;
        this.password = password;
    }

    @DbField("name")
    public String name;
    @DbField("password")
    public String password;

}
