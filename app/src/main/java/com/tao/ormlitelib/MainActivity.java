package com.tao.ormlitelib;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tao.ormlib.version.VersionBean;
import com.tao.ormlib.version.VersionDao;
import com.tao.ormlitelib.ben.PersionBean;
import com.tao.ormlitelib.ben.UserBean;
import com.tao.ormlitelib.dao.PersionDao;
import com.tao.ormlitelib.dao.UserDao;

import java.io.File;
import java.io.IOException;
import java.util.List;

//import com.scwang.smart.refresh.layout.SmartRefreshLayout;

public class MainActivity extends AppCompatActivity {
    private UserDao userDao;
    private String tag = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,100);
        }
        userDao = new UserDao(this, 2);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "123" + File.separator + "123.test");
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    int userId = 0;
    public void addUser(View view) {
        userId++;
        UserBean data = new UserBean("user" + userId, userId);
        int add = userDao.add(data);
        showResult(" addUser " + add);
        Log.e(tag, userDao.queryAll().toString());
    }

    public void showResult(final String s) {
        Log.e(tag, "showResult:" + s);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void check_version(View view) {
        VersionDao versionDao = new VersionDao(this);
        List<VersionBean> versionBeans = versionDao.queryAll();
        Log.e(tag, "check_version " + (versionBeans == null ? "null" : versionBeans.toString()));

    }

    public void addPersion(View view) {
        PersionDao persionDao = new PersionDao(this,PersionDao.Version);
        persionDao.addIfNotExists(new PersionBean("人员1", "地址", 13));
        List<PersionBean> persionBeans = persionDao.queryAll();
        Log.e(tag, "check_version " + (persionBeans == null ? "null" : persionBeans.toString()));

    }
}
