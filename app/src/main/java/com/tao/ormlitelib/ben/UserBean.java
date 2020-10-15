package com.tao.ormlitelib.ben;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class UserBean {
    public UserBean() {
    }

    public UserBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // generatedId自增id ， columnName表名 ， unique唯一值，
    @DatabaseField(generatedId = true, columnName = "myid", unique = true)
    int id;
    @DatabaseField(columnName = "name")
    String name;
    @DatabaseField(columnName = "age")
    int age;

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
