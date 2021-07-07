package org.vuffy.o2o.dao;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.WechatAuth;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WechatAuthDaoTest extends BaseTest {
    @Autowired
    private WechatAuthDao wechatAuthDao;

    @Test
    public void testAInsertWechatAuth() throws Exception {
        // 测试：新增一条微信账号的信息
        WechatAuth wechatAuth = new WechatAuth();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(1L);
        // 给微信账号绑定上用户信息
        wechatAuth.setPersonInfo(personInfo);
        // 指定个openId
        wechatAuth.setOpenId("jdlkjfksjlfjs");
        wechatAuth.setCreateTime(new Date());
        int effectedNum = wechatAuthDao.insertWechatAuth(wechatAuth);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testBQueryWechatAuthByOpenId() throws Exception {
        WechatAuth wechatAuth = wechatAuthDao.queryWechatInfoByOpenId("jdlkjfksjlfjs");
        System.out.println(wechatAuth.getPersonInfo().getUserName());
    }


}
