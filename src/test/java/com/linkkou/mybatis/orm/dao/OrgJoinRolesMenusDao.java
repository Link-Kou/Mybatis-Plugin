package com.linkkou.mybatis.orm.dao;

import com.linkkou.mybatis.orm.domain.OrgJoinRolesMenusDomain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色管理菜单(OrgJoinRolesMenus)表数据库访问层
 *
 * @author lk
 * @version 1.0
 * @date 2020-10-15 09:56:12
 */
@Repository
public interface OrgJoinRolesMenusDao {

    /**
     * 通过ID查询单条数据
     *
     * @param fRows 主键
     * @return 实例对象
     */
    OrgJoinRolesMenusDomain queryById(@Param("fId") Integer fRows);

    OrgJoinRolesMenusDomain queryById2(@Param("fId") Integer fRows);


    OrgJoinRolesMenusDomain queryForId(@Param("sd") String sd, @Param("list") List<Integer> fRows);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<OrgJoinRolesMenusDomain> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param orgJoinRolesMenusDomain 实例对象
     * @return 对象列表
     */
    List<OrgJoinRolesMenusDomain> queryAll(OrgJoinRolesMenusDomain orgJoinRolesMenusDomain);

    /**
     * 新增数据
     *
     * @param orgJoinRolesMenusDomain 实例对象
     * @return 影响行数
     */
    int insert(OrgJoinRolesMenusDomain orgJoinRolesMenusDomain);

    /**
     * 修改数据
     *
     * @param orgJoinRolesMenusDomain 实例对象
     * @return 影响行数
     */
    int update(OrgJoinRolesMenusDomain orgJoinRolesMenusDomain);

    /**
     * 通过主键删除数据
     *
     * @param fId 主键
     * @return 影响行数
     */
    int deleteById(String fId);

    /**
     * 通过角色id删除数据
     *
     * @param fRolesId 主键
     * @return 影响行数
     */
    int deleteByRoleId(String fRolesId);

}