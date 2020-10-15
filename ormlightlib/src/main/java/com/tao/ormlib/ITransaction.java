package com.tao.ormlib;

public interface ITransaction {
    /**
     * 事物操作
     * @param dao
     */
    void transaction(IDao dao);
}
