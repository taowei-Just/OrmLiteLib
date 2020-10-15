package com.tao.ormlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tao.ormlib.version.VersionBean;
import com.tao.ormlib.version.VersionDao;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库操作管理工具类
 * <p>
 * 我们需要自定义一个类继承自ORMlite给我们提供的OrmLiteSqliteOpenHelper，创建一个构造方法，重写两个方法onCreate()和onUpgrade()
 * 在onCreate()方法中使用TableUtils类中的createTable()方法初始化数据表
 * 在onUpgrade()方法中我们可以先删除所有表，然后调用onCreate()方法中的代码重新创建表
 * <p>
 * 我们需要对这个类进行单例，保证整个APP中只有一个SQLite Connection对象
 * <p>
 * 这个类通过一个Map集合来管理APP中所有的DAO，只有当第一次调用这个DAO类时才会创建这个对象（并存入Map集合中）
 * 其他时候都是直接根据实体类的路径从Map集合中取出DAO对象直接调用
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    static String tag = "DatabaseHelper";
    private static Map<String, DatabaseHelper> databaseHelperMap = new HashMap<>();
    // 数据库名称
    public static final String DATABASE_NAME = "mydb.db";
    // 本类的单例实例
    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();
    private Map<String, BaseDao> baseDaoMps = new HashMap<>();
    private Class[] beanClassS = new Class[0];
    private int status = 0;  // 1 create 2. onUpgrade
    private ConnectionSource connectionSource;
    private SQLiteDatabase database;
    private String tablePath = DATABASE_NAME;
    Context context;
    private int version;
    private String signName="tao";

    /**
     * 第一创建默认的数据库时使用 可创建多个不同路径的数据库
     *
     * @param context
     * @param classS
     * @return
     */
    // 获取本类单例对象的方法
    public static synchronized DatabaseHelper getInstance(Context context, String dataBaseName, Class[] classS) {
        if (databaseHelperMap.get(dataBaseName) == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelperMap.get(dataBaseName) == null) {
                    File file = new File(dataBaseName);
                    if (dataBaseName.contains(File.separator) && !file.exists()) {
                        boolean mkdirs = file.getParentFile().mkdirs();
                        Log.e(tag, dataBaseName + "   " + file.getParent() + "  " + mkdirs);
                    }
                    databaseHelperMap.put(dataBaseName, new DatabaseHelper(context.getApplicationContext(), dataBaseName, classS));
                }
            }
        }
        return databaseHelperMap.get(dataBaseName);
    }

    /**
     * @param context
     * @param dataBaseName 数据库名称
     * @param classS       数据库中表对应的类集合
     * @param version      数据库版本
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context, String dataBaseName, Class[] classS, int version) {
        if (databaseHelperMap.get(dataBaseName) == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelperMap.get(dataBaseName) == null) {
                    File file = new File(dataBaseName);
                    if (dataBaseName.contains(File.separator) && !file.exists()) {
                        boolean mkdirs = file.getParentFile().mkdirs();
                        Log.e(tag, dataBaseName + "   " + file.getParent() + "  " + mkdirs);
                    }
                    databaseHelperMap.put(dataBaseName, new DatabaseHelper(context.getApplicationContext(), dataBaseName, classS, version));
                }
            }
        }
        return databaseHelperMap.get(dataBaseName);
    }

    /**
     * 第一创建默认的数据库时使用
     *
     * @param context
     * @param classS
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context, Class[] classS) {
        return getInstance(context, DATABASE_NAME, classS);
    }


    // 私有的构造方法
    private DatabaseHelper(Context context, String tableName, Class[] classS, int version) {
        super(context, tableName, null, version);
        if (context != null)
            this.context = context;
        if (classS == null || classS.length == 0)
            return;
        this.beanClassS = classS;
        this.tablePath = tableName;
        this.version = version;
        Log.e(tag, "init status " + status + " Thread " + Thread.currentThread());

    }    // 私有的构造方法

    private DatabaseHelper(Context context, String tableName, Class[] classS) {
        this(context, tableName, classS, 1);
    }

    // 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }
//
//    @Override
//    public SQLiteDatabase getWritableDatabase() {
//        if ((tablePath.contains(File.separator))) {
//            return SQLiteDatabase.openDatabase(tablePath, null, SQLiteDatabase.OPEN_READWRITE);
//        }
//        return super.getWritableDatabase();
//    }
//
//    @Override
//    public SQLiteDatabase getReadableDatabase() {
//        if ((tablePath.contains(File.separator))) {
//            return SQLiteDatabase.openDatabase(tablePath, null, SQLiteDatabase.OPEN_READONLY);
//        }
//        return super.getReadableDatabase();
//    }

    @Override // 创建数据库时调用的方法
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        this.database = database;
        Log.e(tag, "onCreate status " + status + " Thread " + Thread.currentThread());
        creat(connectionSource);

        try {
            updataDbVersionRecode(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置或者更新 表名对应的版本号
     *
     * @param newVersion
     * @throws Exception
     */

    private void updataDbVersionRecode(int newVersion) throws Exception {
        Log.e(tag, "updataDbVersionRecode " + newVersion   + " databaseName  " +getDatabaseName());
        // 1.得到当前表名称 
        // 2.获取当前版本号
        // 3.添加或者更新当前表名对应的版本号
        
        
        if ( VersionDao.dbName.equals( getDatabaseName()))
            return;
        
        VersionBean versionBean = new VersionBean( getDatabaseName(), version, signName);

        VersionDao versionDao = new VersionDao(context);
        versionDao.addOrUpdateByWhere(versionBean, new QueryWhere() {
            @Override
            public Where where(Where where) throws Exception {
                return where.eq(VersionBean.tableName_tableName ,getDatabaseName());
            }
        });


    }

    private int getOtherDaoversion() {
        int version = BaseDao.DEFAULT_VERSION;

        for (String tableName : databaseHelperMap.keySet()) {
            Map<String, Dao> daos = databaseHelperMap.get(tableName).daos;
            

        }

        for (String s : daos.keySet()) {
            Dao dao = daos.get(s);
            if (!(dao instanceof BaseDao))
                continue;
            int dbVersion = ((BaseDao) dao).getDBVersion(version);
            version = dbVersion > version ? dbVersion : version;
        }
        return version;
    }

    /**
     * 数据库升级回调
     *
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */

    @Override // 数据库版本更新时调用的方法
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        this.connectionSource = connectionSource;
        this.database = database;
        Log.e(tag, "onUpgrade status " + status + "oldVersion  " + oldVersion + "  newVersion  " + newVersion + " Thread " + Thread.currentThread());
        upgrade(database, connectionSource, oldVersion, newVersion);
        try {
            updataDbVersionRecode(newVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     *
     * @param connectionSource
     */
    private void creat(ConnectionSource connectionSource) {
        try {
            for (Class classS : beanClassS) {
                TableUtils.createTableIfNotExists(connectionSource, classS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 升级数据库，
     *
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    private void upgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.e(tag, " upgrade oldVersion " + oldVersion + "newVersion " + newVersion);
        try {
            for (Class classS : beanClassS) {

                if (DatabaseUtil.tableIsExist(database, classS)) {
                    Log.e(tag, "  存在表" + classS);
                } else {
                    Log.e(tag, "  不存在表" + classS);
                    TableUtils.createTableIfNotExists(connectionSource, classS);
                }

                if (newVersion > 1) {
                    DatabaseUtil.upgradeTable(database, connectionSource, classS, DatabaseUtil.OPERATION_TYPE.ADD);
                }
            }
//            onCreate(database, connectionSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
        beanClassS = null;
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }


    public <T> void putBaseDao(Class<T> clazz, BaseDao<T> tBaseDao) throws SQLException {
        tBaseDao.setDao(getDao(clazz));

        baseDaoMps.put(clazz.getSimpleName(), tBaseDao);
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

  
}

