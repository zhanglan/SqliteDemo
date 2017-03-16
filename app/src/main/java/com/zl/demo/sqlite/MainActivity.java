package com.zl.demo.sqlite;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zl.demo.sqlite.db.BaseDaoFactory;
import com.zl.demo.sqlite.db.IBaseDao;
import com.zl.demo.sqlite.db.annotion.DbField;

import java.util.List;

public class MainActivity extends Activity {
    private static final String Tag = MainActivity.class.getSimpleName();
    IBaseDao<User> baseDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
    }

    public void save(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                Long start = System.currentTimeMillis();
                Log.e("MainActivity","开始插入");
                for(int i=0; i<10; i++){
                    user.setName(""+(i+1));
                    Long l = baseDao.insert(user);
                    Log.i("MainActivity","l-->"+l);
                }
                Log.e("MainActivity","插入结束");
                Log.e("MainActivity","耗时"+(System.currentTimeMillis()-start)+"ms");
            }
        }).start();
    }
    public void update(View view){
        User value = new User();
        value.setPassword("aaaa");
        User where = new User();
        where.setName("123");
        int result = baseDao.update(value,where);
        Log.e(Tag, "update result-->"+result);
    }

    public void updateSql(View view){
        User value = new User();
        value.setPassword("bbbb");
        String where = " 1=1 and name<20 ";
        int result = baseDao.update(value,where);
        Log.e(Tag, "updateSql result-->"+result);
    }

    public void delete(View view){
        User where = new User();
        where.setPassword("aaaa");
        int result = baseDao.delete(where);
        Log.e(Tag, "delete result-->"+result);
    }

    public void deleteSql(View view){
        String where = " 1=1 and name<20";
        int result = baseDao.delete(where);
        Log.e(Tag, "deleteSql result-->"+result);
    }

    public void query(View view){
        User where = new User();
        where.setPassword("123456");
        List<User> list = baseDao.query(where);
        for(User u: list){
            Log.i(Tag,"name-->"+u.getName()+"   password-->"+u.getPassword());
        }
    }

    public void querySql(View view){
        String sql = "select name from tb_user where name<20";
        List<User> list = baseDao.query(sql);
        for(User u: list){
            Log.i(Tag,"name-->"+u.getName()+"   password-->"+u.getPassword());
        }
    }

    public void rawQuery(View view){
        String sql = "select * from tb_user where name<20";
        String res = baseDao.rawQuery(sql);
        Log.i(Tag, "rawQuery result-->"+res);
    }
}
