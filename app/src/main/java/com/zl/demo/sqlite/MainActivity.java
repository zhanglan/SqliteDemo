package com.zl.demo.sqlite;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zl.demo.sqlite.db.BaseDaoFactory;
import com.zl.demo.sqlite.db.IBaseDao;

public class MainActivity extends Activity {
    IBaseDao<User> baseDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
//        openOrCreateDatabase("test.db",0,null);
    }

    public void save(View view){
        User user = new User("zhangsan","123456");
        baseDao.insert(user);
    }
}
