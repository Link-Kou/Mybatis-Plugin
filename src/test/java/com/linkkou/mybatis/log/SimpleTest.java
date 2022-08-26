package com.linkkou.mybatis.log;

import com.linkkou.mybatis.orm.dao.OrgJoinRolesMenusDao;
import com.linkkou.mybatis.orm.dao.OrgJoinRolesMenusDaoMapper;
import com.linkkou.mybatis.orm.domain.OrgJoinRolesMenusDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class SimpleTest {

    @Autowired
    private OrgJoinRolesMenusDao orgJoinRolesMenusDao;

    @Autowired
    private OrgJoinRolesMenusDaoMapper orgJoinRolesMenusDaoMapper;


    @Test
    public void doTest() {
        orgJoinRolesMenusDao.queryById(1);
    }

    @Test
    public void doTest1_1() {
        orgJoinRolesMenusDaoMapper.queryById(1);
        orgJoinRolesMenusDao.queryById(1);
    }

    @Test
    public void doTest2() {
        orgJoinRolesMenusDao.queryAll(new OrgJoinRolesMenusDomain().setFRolesId("hisMatch_2020, (String), ").setFMenusId("asdasdase,"));
    }

    @Test
    public void doTest3() {
        final ArrayList<Integer> objects = new ArrayList<>();
        objects.add(1);
        objects.add(2);
        objects.add(3);
        objects.add(4);
        orgJoinRolesMenusDao.queryForId("123123",objects);
    }

}
