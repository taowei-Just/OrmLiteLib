package com.tao.ormlib;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.FieldType;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;
import com.tao.ormlib.version.VersionBean;
import com.tao.ormlib.version.VersionDao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> implements IDao<T> {
    public static final String DEFAULT_DATABASE_NAME = "mydb.db";
    public static int DEFAULT_VERSION = 1;
    public String DB_SIGN_NAME = "tao";
    private Dao dao;
    private Class<T> clazz;
    private int mVersion = DEFAULT_VERSION;
    private DatabaseHelper databaseHelper;
    Context context;
    private String dbName;

    /**
     * @param context
     */
    public BaseDao(Context context) {
        this(context, DEFAULT_DATABASE_NAME, DEFAULT_VERSION);
    }

    /**
     * 数据库版本
     *
     * @param context
     * @param version
     */
    public BaseDao(Context context, int version) {
        this(context, DEFAULT_DATABASE_NAME, version);
    }

    /**
     * @param context
     * @param dbName  数据库名称
     */
    public BaseDao(Context context, String dbName) {
        this(context, dbName, DEFAULT_VERSION);
    }

    /**
     * @param context
     * @param dbName
     * @param version
     */
    public BaseDao(Context context, String dbName, int version) {
        try {
            clazz = getTableClass();
            this.context = context;
            this.dbName = dbName;
            databaseHelper = DatabaseHelper.getInstance(context, dbName, getDbClassS(), getDBVersion(version));
            this.dao = databaseHelper.getDao(clazz);
            databaseHelper.setSignName(DB_SIGN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public String getSignName() {
        return DB_SIGN_NAME;
    }

    public void setSignName(String name) {
        this.DB_SIGN_NAME = name;
        if (null != databaseHelper)
            databaseHelper.setSignName(DB_SIGN_NAME);
    }

    public int getDBVersion(int version) {
        dbRecodeVersion();
        this.mVersion = version > mVersion ? (version > DEFAULT_VERSION ? version : DEFAULT_VERSION) : mVersion;
        return mVersion;
    }

    private void dbRecodeVersion() {
        try {
            VersionDao versionDao = new VersionDao(context);
            List<VersionBean> versionBeans = versionDao.queryByWhere(new QueryWhere() {
                @Override
                public Where where(Where where) throws Exception {
                    return where.eq(VersionBean.tableName_tableName, dbName);
                }
            });
            if (versionBeans != null && versionBeans.size() > 0) {
                for (VersionBean versionBean : versionBeans) {
                    this.mVersion = versionBean.getVersion() > this.mVersion ? versionBean.getVersion() : this.mVersion;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库内表对应的类集合
     *
     * @return
     */
    protected abstract Class[] getDbClassS();

    /**
     * 获取数据库表对应的 类
     *
     * @return
     */
    protected Class<T> getTableClass() throws Exception {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }


    /**
     * 获取dao对象
     *
     * @return
     */
    public Dao getDao() {
        return dao;
    }

    @Override
    public int add(List<T> data) {
        if (dao == null)
            return -1;
        try {
            return dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int add(T data) {
        if (dao == null)
            return -1;
        try {
            return dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public T addIfNotExists(T data) {
        if (dao == null)
            return null;
        try {
            return (T) dao.createIfNotExists(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public int delete(T data) {
        if (dao == null)
            return -1;
        try {
            return dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;

    }

    public int deleteByWhere(DeleteWhere where) {
        if (dao == null)
            return -1;
        DeleteBuilder deleteBuilder = getDeleteBuilder();
        try {
            deleteBuilder.setWhere(where.where(deleteBuilder.where()));
            return deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public final QueryBuilder getQueryBuilder() {
        if (dao == null)
            return null;
        return dao.queryBuilder();
    }

    public final UpdateBuilder getUpdateBuilder() {
        if (dao == null)
            return null;
        return dao.updateBuilder();
    }

    public final DeleteBuilder getDeleteBuilder() {
        if (dao == null)
            return null;
        return dao.deleteBuilder();
    }

    @Override
    public int update(T data) {
        if (dao == null)
            return -1;
        try {
            return dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据条件更新 所有列
     *
     * @param data
     * @param where
     * @return
     */
    public int updateByWhere(T data, UpdataWhere where) {
        if (dao == null)
            return -1;
        try {
            UpdateBuilder updateBuilder = getUpdateBuilder();
            FieldType[] fieldTypes = dao.getTableInfo().getFieldTypes();
            for (FieldType fieldType : fieldTypes) {
                if (fieldType.isGeneratedId()) continue;
                String columnName = fieldType.getColumnName();
                String fieldName = fieldType.getFieldName();
                updateBuilder.updateColumnValue(columnName, ClassUtil.getValue(data, fieldName));
            }
            updateBuilder.setWhere(where.where(updateBuilder.where()));
            return updateBuilder.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据条件更新指定列
     *
     * @param updatePerms
     * @param where
     * @return
     */
    public int updateByWhere(Map<String, Object> updatePerms, UpdataWhere where) {
        if (dao == null)
            return -1;
        try {
            UpdateBuilder updateBuilder = getUpdateBuilder();
            updateBuilder.setWhere(where.where(updateBuilder.where()));
            setColumValues(updateBuilder, updatePerms);
            return updateBuilder.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    private void setColumValues(UpdateBuilder updateBuilder, Map<String, Object> updatePerms) throws Exception {
        Field[] justReflect = ClassUtil.getDeclaredFieldsJustReflect(clazz);
        Iterator<String> iterator = updatePerms.keySet().iterator();
        FieldType[] fieldTypes = dao.getTableInfo().getFieldTypes();

        while (iterator.hasNext()) {
            String next = iterator.next();
            Object o = updatePerms.get(next);
            boolean hasField = false;
            FieldType fieldType1 = null;
            for (FieldType fieldType : fieldTypes) {
                if (fieldType.getFieldName().equals(next)) {
                    hasField = true;
                    fieldType1 = fieldType;
                    break;
                }
            }
            if (!hasField) {
                throw new Exception("not has cloumName " + next);
            }
            if (fieldType1.isGeneratedId())
                continue;
            updateBuilder.updateColumnValue(next, o);
        }
    }

    @Override
    public List<T> queryAll() {
        if (dao == null)
            return null;
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T queryById(int id) {
        if (dao == null)
            return null;
        try {
            return (T) dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据条件查询
     *
     * @param queryWhere
     * @return
     */
    public List<T> queryByWhere(QueryWhere queryWhere) {
        List<T> query = null;
        try {
            QueryBuilder queryBuilder = getQueryBuilder();
            queryBuilder.setWhere(queryWhere.where(queryBuilder.where()));
            try {
                if (!TextUtils.isEmpty(queryWhere.orderBy()))
                    queryBuilder.orderBy(queryWhere.orderBy(), queryWhere.ascending());
                query = queryBuilder.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

    @Override
    public Dao.CreateOrUpdateStatus addOrUpdate(T data) {
        if (dao == null || data == null)
            return null;
        try {
            return dao.createOrUpdate(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int addOrUpdateByWhere(T data, final QueryWhere queryWhere) {
        List<T> ts = queryByWhere(queryWhere);
        if (ts == null || ts.size() == 0) {
            return add(data);
        } else {
            return updateByWhere(data, new UpdataWhere() {
                @Override
                public Where where(Where where) throws Exception {
                    return queryWhere.where(where);
                }
            });
        }

    }

    @Override
    public boolean uesTransaction(ITransaction transaction) {
        if (dao == null || transaction == null)
            return false;
        DatabaseConnection dc = null;//也可以 new AndroidDatabaseConnection
        try {
            dc = dao.startThreadConnection();
            dc.setAutoCommit(false); //设置不自动提交
            Savepoint savePoint = dc.setSavePoint("savePointName");//设置回滚点，参数是一个名字，没什么影响
            transaction.transaction(this);
            dc.commit(savePoint);//提交事务。保存大量数据时以事务的方式提交，可以大幅提高速度
            dao.endThreadConnection(dc);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
