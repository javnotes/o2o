package org.vuffy.o2o.service;

import org.vuffy.o2o.entity.PersonInfo;

public interface PersonInfoService {

    /**
     * 根据userId获取personInfo
     * @param userId
     * @return
     */
    PersonInfo getPersonInfoById(Long userId);

}