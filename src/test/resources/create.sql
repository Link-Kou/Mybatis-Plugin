-- cms.t_org_role_menus definition

CREATE TABLE `t_org_role_menus`
(
    `rows`        int                                                   NOT NULL AUTO_INCREMENT,
    `f_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `f_role_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '角色id',
    `f_menus_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '菜单id',
    `f_check`     tinyint                                               NOT NULL DEFAULT '0' COMMENT '1=true 0=false (与节点有上下级关系的节点)',
    `createtime`  timestamp                                             NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updatedtime` timestamp                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`rows`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色菜单权限';