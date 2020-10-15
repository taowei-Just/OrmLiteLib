package com.tao.ormlib.version;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

@DatabaseTable(tableName = "version_info")
public class VersionBean {

    public static final String columnName_id = "id";
    public static final String tableName_version = "version";
    public static final String tableName_time = "time";
    public static final String tableName_timeStemp = "timeStemp";
    public static final String tableName_tableName = "tableName";


    public VersionBean() {
    }

    //id
    @DatabaseField(generatedId = true, columnName = "id", unique = true)
    int id;
    // 当前数据库版本
    @DatabaseField(columnName = "version")
    int version;
    // 格式化事件戳
    @DatabaseField(columnName = "time")
    String timeStemp;
    // 时间毫秒数
    @DatabaseField(columnName = "timeStemp")
    long time;
    // 署名
    @DatabaseField(columnName = "signName")
    String signName;
    @DatabaseField(columnName = "tableName")
    String tableName;

    public VersionBean(String tableName, int version, String signName) {
        this.tableName = tableName;
        this.version = version;
        this.time = System.currentTimeMillis();
        this.signName = signName;
        this.timeStemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date(time));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTimeStemp() {
        return timeStemp;
    }

    public void setTimeStemp(String timeStemp) {
        this.timeStemp = timeStemp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "id=" + id +
                ", version=" + version +
                ", timeStemp='" + timeStemp + '\'' +
                ", time=" + time +
                ", signName='" + signName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
