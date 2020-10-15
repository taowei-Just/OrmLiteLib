package com.tao.ormlib;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.util.List;
import java.util.Map;

public interface IDao<T> {

    // 向表中添加一条数据
    int add(T data);

    // 向表中添加一组数据
    int add(List<T> data);

    /**
     *     如果不存在则添加 存在则不操作
     */
    T addIfNotExists(T data);

    // 删除表中的一条数据 根据id 删除
    int delete(T data);


    // 修改表中的一条数据 根据id 更新
    int update(T data);

    // 查询user表中的所有数据
    List<T> queryAll();

    // 根据ID取出用户信息
    T queryById(int id);

    /**
     * 使用事务
     *
     * @param transaction
     * @return
     */
    boolean uesTransaction(ITransaction transaction);

    /**
     * 存在则更新不存在则添加
     *
     * @param data
     * @return
     */
    Dao.CreateOrUpdateStatus addOrUpdate(T data);

    /**
     * 根据条件   存在则更新不存在则添加
     *
     * @param data
     * @param queryWhere
     * @return
     */
    int addOrUpdateByWhere(T data, QueryWhere queryWhere);

    /**
     * 根据条件查询
     *
     * @param queryWhere
     * @return
     */
    List<T> queryByWhere(QueryWhere queryWhere);

    /**
     * 根据条件更新
     *
     * @param data
     * @param where
     * @return
     */
    int updateByWhere(T data, UpdataWhere where);

    /**
     * 根据条件更新 指定部分字段
     *
     * @param updatePerms
     * @param where
     * @return
     */
    int updateByWhere(Map<String, Object> updatePerms, UpdataWhere where);

    /**
     * 根据条件删除
     *
     * @param where
     * @return
     */
    int deleteByWhere(DeleteWhere where);
}
