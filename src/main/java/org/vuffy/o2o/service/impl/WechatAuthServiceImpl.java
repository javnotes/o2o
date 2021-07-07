package org.vuffy.o2o.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.dao.PersonInfoDao;
import org.vuffy.o2o.dao.WechatAuthDao;
import org.vuffy.o2o.dto.WechatAuthExecution;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.WechatAuth;
import org.vuffy.o2o.enums.WechatAuthStateEnum;
import org.vuffy.o2o.exceptions.WechatAuthOperationException;
import org.vuffy.o2o.service.WechatAuthService;

import java.util.Date;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private static Logger logger = LoggerFactory.getLogger(WechatAuthServiceImpl.class);

    @Autowired
    private WechatAuthDao wechatAuthDao;

    @Autowired
    private PersonInfoDao personInfoDao;

    @Override
    public WechatAuth getWechatAuthByOpenId(String openId) {
        return wechatAuthDao.queryWechatInfoByOpenId(openId);
    }

    @Override
    @Transactional
    public WechatAuthExecution register(WechatAuth wechatAuth) throws WechatAuthOperationException {
        // 判断空值
        if (wechatAuth == null || wechatAuth.getOpenId() == null) {
            return new WechatAuthExecution(WechatAuthStateEnum.NULL_AUTH_INFO);
        }

        try {
            // 设置账号创建时间
            wechatAuth.setCreateTime(new Date());
            // 如果微信帐号里夹带着用户信息且userId为空，则认为该用户第一次使用平台(且通过微信登录)
            // 则自动创建用户信息
            if (wechatAuth.getPersonInfo() != null && wechatAuth.getPersonInfo().getUserId() == null) {
                try {
                    wechatAuth.getPersonInfo().setCreateTime(new Date());
                    wechatAuth.getPersonInfo().setEnableStatus(1);
                    PersonInfo personInfo = wechatAuth.getPersonInfo();
                    int effectedNum = personInfoDao.insertPersonInfo(personInfo);
                    // TODO userId 是怎么赋值的，MyBatis
                    wechatAuth.setPersonInfo(personInfo);
                    if (effectedNum <= 0) {
                        throw new WechatAuthOperationException("添加用户信息失败。");
                    }
                } catch (Exception e) {
                    logger.error("InsertPersonInfo error:" + e.toString());
                }
            }

            // 将微信账号信息保存在本平台
            int effectedNum = wechatAuthDao.insertWechatAuth(wechatAuth);
            if (effectedNum <= 0) {
                throw new WechatAuthOperationException("账号创建失败。");
            } else {
                return new WechatAuthExecution(WechatAuthStateEnum.SUCCESS, wechatAuth);
            }
        } catch (Exception e) {
            logger.error("InsertWechatAuth error:" + e.toString());
            throw new WechatAuthOperationException("InsertWechatAuth error:" + e.getMessage());
        }
    }
}
