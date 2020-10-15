package com.tao.ormlib;

public abstract class QueryWhere implements IWhere {
    public String orderBy() {
        return "";
    }

    public boolean ascending() {
        return false;
    }
}
