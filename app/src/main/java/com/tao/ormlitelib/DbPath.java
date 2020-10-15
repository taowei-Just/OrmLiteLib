package com.tao.ormlitelib;

import android.os.Environment;

import com.tao.ormlitelib.ben.UserBean;

import java.io.File;

public class DbPath {
    public static final String persionDbPath= Environment.getExternalStorageDirectory()+ File.separator+"ormLiteTest"+File.separator+"persion.db";
    public static final String userDbPath= Environment.getExternalStorageDirectory()+ File.separator+"ormLiteTest"+File.separator+"user.db";
  
    public static final Class[] userDbClass = new Class[]{ UserBean.class};

}
