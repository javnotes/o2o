package org.vuffy.o2o.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.dao.PersonInfoDao;
import org.vuffy.o2o.dto.WechatAuthExecution;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.WechatAuth;
import org.vuffy.o2o.enums.WechatAuthStateEnum;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class WechatAuthServiceTest extends BaseTest {
    @Autowired
    private WechatAuthService wechatAuthService;

    @Test
    public void testRegister() {
        // 测试：新增一条微信账号信息
        WechatAuth wechatAuth = new WechatAuth();
        PersonInfo personInfo = new PersonInfo();
        String openId = "fhjkdahkfhdk";
        // 给微信账号设置用户信息，不设置userId
        // 测试创建新微信账号信息时，自动创建平台的用户信息
        personInfo.setCreateTime(new Date());
        personInfo.setUserName("测试一下");
        personInfo.setUserType(1);
        wechatAuth.setPersonInfo(personInfo);
        wechatAuth.setOpenId(openId);
        wechatAuth.setCreateTime(new Date());
        WechatAuthExecution wechatAuthExecution = wechatAuthService.register(wechatAuth);
        assertEquals(WechatAuthStateEnum.SUCCESS.getState(), wechatAuthExecution.getState());
        // 测试：通过openId找到新增的wechatAuth
        wechatAuth = wechatAuthService.getWechatAuthByOpenId(openId);
        System.out.println(wechatAuth.getPersonInfo().getUserName());

    }
}
