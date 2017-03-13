package com.zl.demo.sqlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zl.demo.sqlite.db.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {
    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
    }
}
