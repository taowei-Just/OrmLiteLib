package com.tao.ormlitelib.ben;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "persion")
public class PersionBean {

    @DatabaseField(columnName = "name"  )
    String name;
    @DatabaseField(columnName = "address")
    String address;
    @DatabaseField(columnName = "age")
    int age;
    @DatabaseField( generatedId = true ,columnName = "id",unique = true)
    int  id;

    public PersionBean() {
    }

    public PersionBean(String name , String address, int age ) {
        this.address = address;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "PersionBean{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", id=" + id +
                '}';
    }
}
