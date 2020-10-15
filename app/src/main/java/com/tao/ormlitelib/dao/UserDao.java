package com.tao.ormlitelib.dao;

import android.content.Context;

import com.tao.ormlib.BaseDao;
import com.tao.ormlitelib.DbPath;
import com.tao.ormlitelib.ben.UserBean;

 

public class UserDao  extends BaseDao<UserBean> {
  
    public static   final String dbName =DbPath.userDbPath;

    public UserDao(Context context) {
        this(context,dbName);
    }
 
    public UserDao(Context context, String dbName) {
        super(context, dbName);
    }

    public UserDao(Context context, int version) {
        super(context, dbName,version);
        setSignName("My_test");
    }
    
    @Override
    protected Class[] getDbClassS() {
        return DbPath.userDbClass;
    }
 
}
