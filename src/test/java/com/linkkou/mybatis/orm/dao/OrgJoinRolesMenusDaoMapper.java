package com.linkkou.mybatis.orm.dao;

import com.linkkou.mybatis.orm.domain.OrgJoinRolesMenusDomain;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色管理菜单(OrgJoinRolesMenus)表数据库访问层
 *
 * @author lk
 * @version 1.0
 * @date 2020-10-15 09:56:12
 */
@Mapper
public interface OrgJoinRolesMenusDaoMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param fRows 主键
     * @return 实例对象
     */
    @Select(" select f_rows, f_id, f_roles_id, f_menus_id, f_check, is_deleted, createtime, updatedtime from t_org_join_roles_menus where f_id = #{fRows}")
    @Results({
            @Result(column = "f_rows",property = "fRows"),
            @Result(column = "f_id",property = "fId"),
            @Result(column = "f_roles_id",property = "fRolesId"),
            @Result(column = "f_menus_id",property = "fMenusId"),
            @Result(column = "createtime",property = "createtime"),
            @Result(column = "updatedtime",property = "updatedtime")
    })
    OrgJoinRolesMenusDomain queryById(@Param("fRows") Integer fRows);

}