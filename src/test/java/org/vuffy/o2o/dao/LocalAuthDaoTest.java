package org.vuffy.o2o.dao;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.LocalAuth;
import org.vuffy.o2o.entity.PersonInfo;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalAuthDaoTest extends BaseTest {

    @Autowired
    private LocalAuthDao localAuthDao;
    private static final String username = "testusername";
    private static final String pwd = "testpassword";

    @Test
    public void testAInsertLocalAuth() throws Exception{
        // 新增一条平台账号信息
        LocalAuth localAuth = new LocalAuth();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1L);
        // 给平台账号绑定上用户信息
        localAuth.setPersonInfo(personInfo);
        // 设置用户账号和密码
        localAuth.setUsername(username);
        localAuth.setPassword(pwd);
        localAuth.setCreateTime(new Date());
        int effectedNum = localAuthDao.insertLocalAuth(localAuth);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testBQueryByUserNameAndPwd() throws Exception{
        // 通过 用户账号+密码查询用户信息
        LocalAuth localAuth = localAuthDao.queryLocalByUserNameAndPwd(username, pwd);
        System.out.println(localAuth.getUsername());

        System.out.println(localAuth.toString());
        System.out.println(localAuth.getPersonInfo().toString());

        assertEquals("测试用户1", localAuth.getPersonInfo().getUserName());
    }

    @Test
    public void testCQueryLocalByUserId() throws Exception {
        // 按 userId 查询平台账号，进而获取用户信息
        LocalAuth localAuth = localAuthDao.queryLocalByUserId(1L);
        assertEquals("测试用户1", localAuth.getPersonInfo().getUserName());
    }

    @Test
    public void testDUpdateLocalAuth() throws Exception{
        // 根据 userId、平台账号、原密码来修改平台账号的密码
        Date now = new Date();
        int effectedNum = localAuthDao.updateLocalAuth(1L, username,pwd,pwd + "new", now);
        assertEquals(1, effectedNum);
        // 查询出该平台账号的最新信息
        LocalAuth localAuth = localAuthDao.queryLocalByUserId(1L);
        // 查看修改后的密码
        System.out.println(localAuth.getPassword());
    }
}
