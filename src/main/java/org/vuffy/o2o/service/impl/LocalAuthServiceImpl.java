package org.vuffy.o2o.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.dao.LocalAuthDao;
import org.vuffy.o2o.dto.LocalAuthExecution;
import org.vuffy.o2o.entity.LocalAuth;
import org.vuffy.o2o.enums.LocalAuthStateEnum;
import org.vuffy.o2o.exceptions.LocalAuthOperationException;
import org.vuffy.o2o.service.LocalAuthService;
import org.vuffy.o2o.util.MD5;

import java.util.Date;
@Service
public class LocalAuthServiceImpl implements LocalAuthService {

    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    @Autowired
    private LocalAuthDao localAuthDao;

    @Override
    public LocalAuth getLocalAuthByUsernameAndPwd(String username, String password) {
        return localAuthDao.queryLocalByUserNameAndPwd(username, MD5.getMD5(password));
    }

    @Override
    public LocalAuth getLocalAuthByUserId(Long userId) {
        return localAuthDao.queryLocalByUserId(userId);
    }

    @Override
    @Transactional
    public LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws LocalAuthOperationException {
        // 空值判断：传入的localAuth的账号密码及其userId不能为空
        if (localAuth == null || localAuth.getPassword() == null || localAuth.getUsername() == null
                || localAuth.getPersonInfo() == null || localAuth.getPersonInfo().getUserId() == null) {
            return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
        }

        // 查询此账号名是否以绑定过平台账号
        LocalAuth tempLocalAuth = localAuthDao.queryLocalByUserId(localAuth.getPersonInfo().getUserId());
        if (tempLocalAuth != null) {
            // 绑定过账号，则不可再进行绑定，以保证平台账号的唯一性
            return new LocalAuthExecution(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
        }

        try {
            // 创建一个平台账号，并与该用户进行绑定
            localAuth.setCreateTime(new Date());
            localAuth.setLastEditTime(new Date());
            // 对用户名对应的密码进行MD5加密
            localAuth.setPassword(MD5.getMD5(localAuth.getPassword()));
            int effectedNum = localAuthDao.insertLocalAuth(localAuth);
            // 是否创建成功
            if (effectedNum <= 0) {
                throw new LocalAuthOperationException("账号绑定失败");
            } else {
                return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new LocalAuthOperationException("insertLocalAuth error:" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LocalAuthExecution modifyLocalAuth(Long userId, String username, String password, String newPassword) throws LocalAuthOperationException {
        // 非空判断
        if (userId == null || username == null || password == null || newPassword == null ||
                password.equals(newPassword)) {
            return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
        }

        try {
            // 更新密码
            int effectedNum = localAuthDao.updateLocalAuth(userId, username, MD5.getMD5(password), MD5.getMD5(newPassword), new Date());
            // 是否更新成功
            if (effectedNum <= 0) {
                throw new LocalAuthOperationException("更新密码失败");
            }
            return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new LocalAuthOperationException("更新密码失败：" + e.getMessage());
        }
    }
}
