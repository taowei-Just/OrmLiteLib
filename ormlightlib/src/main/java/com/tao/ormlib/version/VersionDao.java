package com.tao.ormlib.version;

import android.content.Context;

import com.tao.ormlib.BaseDao;

public final class VersionDao extends BaseDao<VersionBean> {
    public final static String dbName ="my_db_s_inner_version";
    public VersionDao(Context context) {
        super(context ,dbName);
        setSignName("my_Version");
    }

    @Override
    protected Class[] getDbClassS() {
        return new Class[]{VersionBean.class};
    }
    
}
