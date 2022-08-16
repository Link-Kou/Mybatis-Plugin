package com.linkkou.mybatis.orm.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 角色管理菜单(OrgJoinRolesMenus)实体类
 *
 * @author lk
 * @version 1.0
 * @date 2020-10-15 09:56:11
 */
@Data
@Accessors(chain = true)
public class OrgJoinRolesMenusDomain {

    private Integer fRows;
    private String fId;
    /**
     * 角色id
     */
    private String fRolesId;
    /**
     * 菜单id
     */
    private String fMenusId;
    /**
     * 创建时间
     */
    private Date createtime;
    private Date updatedtime;
}