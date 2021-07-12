package org.vuffy.o2o.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.dao.LocalAuthDao;
import org.vuffy.o2o.dto.LocalAuthExecution;
import org.vuffy.o2o.entity.LocalAuth;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.enums.WechatAuthStateEnum;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class LocalAuthServiceTest extends BaseTest {
    @Autowired
    private LocalAuthService localAuthService;

    @Test
    public void testBindLocalAuth() {
        // 新增一条平台账号
        LocalAuth localAuth = new LocalAuth();
        PersonInfo personInfo = new PersonInfo();
        String username = "testusername";
        String password = "testpassword";
        // 给平台账号设置用户信息
        // 设置uerId，表示是该用户创建的账号
        personInfo.setUserId(1L);
        // 给平台账号设置用户信息，表明是与哪个用户绑定
        localAuth.setPersonInfo(personInfo);
        localAuth.setUsername(username);
        localAuth.setPassword(password);

        LocalAuthExecution localAuthExecution = localAuthService.bindLocalAuth(localAuth);

        assertEquals(WechatAuthStateEnum.SUCCESS.getState(), localAuthExecution.getState());
        // 通过userId查找新增的localAuth
        localAuth = localAuthService.getLocalAuthByUserId(personInfo.getUserId());
        System.out.println(localAuth.getPersonInfo().getUserName());
        System.out.println(localAuth.getPassword());
    }

    @Test
    public void testModifyLocalAuth() {
        long userId = 1L;
        String username = "testusername";
        String password = "testpassword";
        String newPassword = "newtestpassword";

        LocalAuthExecution localAuthExecution = localAuthService.modifyLocalAuth(userId, username,password,newPassword);
        assertEquals(WechatAuthStateEnum.SUCCESS.getState(), localAuthExecution.getState());

        LocalAuth localAuth = localAuthService.getLocalAuthByUsernameAndPwd(username, newPassword);

        System.out.println(localAuth.getPersonInfo().getUserName());
    }

}
