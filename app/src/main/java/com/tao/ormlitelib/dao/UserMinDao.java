package com.tao.ormlitelib.dao;

import android.content.Context;

import com.tao.ormlib.BaseDao;
import com.tao.ormlitelib.DbPath;

public class UserMinDao extends BaseDao {
    public UserMinDao(Context context) {
        super(context);
    }

    public UserMinDao(Context context, int version) {
        super(context, version);
    }

    public UserMinDao(Context context, String dbName) {
        super(context, dbName);
    }

    public UserMinDao(Context context, String dbName, int version) {
        super(context, dbName, version);
    }
    

    @Override
    protected Class[] getDbClassS() {
        return DbPath.userDbClass;
    }
}
