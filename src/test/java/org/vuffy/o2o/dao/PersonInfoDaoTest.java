package org.vuffy.o2o.dao;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.PersonInfo;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersonInfoDaoTest extends BaseTest {
    @Autowired
    private PersonInfoDao personInfoDao;

    @Test
    public void testAInsertPersonInfo() throws Exception {
        // 设置新增用户的信息
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserName("测试新增用户");
        personInfo.setGender("女");
        personInfo.setUserType(1);
        personInfo.setCreateTime(new Date());
        personInfo.setLastEditTime(new Date());
        personInfo.setEnableStatus(1);
        int effectedNum = personInfoDao.insertPersonInfo(personInfo);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testBQueryPersonInfoById() {
        // 查询Id=1的用户信息
        long userId = 1L;
        PersonInfo person = personInfoDao.queryPersonInfoById(userId);
        System.out.println(person.getUserName());

    }


}
