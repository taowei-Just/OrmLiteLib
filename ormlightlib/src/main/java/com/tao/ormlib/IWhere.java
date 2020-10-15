package com.tao.ormlib;

import com.j256.ormlite.stmt.Where;

public interface IWhere {
    /**
     * 条件操作
     * @param where
     * @return
     * @throws Exception
     */
    Where where(Where where) throws Exception;
}
