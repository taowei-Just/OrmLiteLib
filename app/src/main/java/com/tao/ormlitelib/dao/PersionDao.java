package com.tao.ormlitelib.dao;

import android.content.Context;

import com.tao.ormlib.BaseDao;
import com.tao.ormlitelib.DbPath;
import com.tao.ormlitelib.ben.PersionBean;

public class PersionDao extends BaseDao<PersionBean> {
    public static final String mDbName = DbPath.persionDbPath;
    public static int Version = 2;

    public PersionDao(Context context, int v) {
        super(context, mDbName, v);
    }

    public PersionDao(Context context) {
        super(context, mDbName, BaseDao.DEFAULT_VERSION);
    }

    @Override
    protected Class[] getDbClassS() {
        return new Class[]{PersionBean.class};
    }
}
