package org.vuffy.o2o.dao;

import org.apache.ibatis.annotations.Param;
import org.vuffy.o2o.entity.LocalAuth;

import java.util.Date;

public interface LocalAuthDao {
    /**
     * 通过账号+密码，查询对应使用，在登录时使用
     *
     * @param username
     * @param password
     * @return
     */
    LocalAuth queryLocalByUserNameAndPwd(@Param("username") String username, @Param("password") String password);

    /**
     * 通过 userId 查询对应 localAuth
     *
     * @param userId
     * @return
     */
    LocalAuth queryLocalByUserId(@Param("userId") long userId);

    /**
     * 添加平台账号
     *
     * @param localAuth
     * @return
     */
    int insertLocalAuth(LocalAuth localAuth);

    /**
     * 通过 userId、userName、password 更改密码
     *
     * @param userId
     * @param username
     * @param password
     * @param newPassword
     * @param lastEditTime
     * @return
     */
    int updateLocalAuth(@Param("userId") Long userId, @Param("username") String username,
                        @Param("password") String password, @Param("newPassword") String newPassword,
                        @Param("lastEditTime") Date lastEditTime);
}
