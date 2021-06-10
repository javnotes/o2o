package org.vuffy.o2o.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vuffy.o2o.dao.AreaDao;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.service.AreaService;

import java.util.List;


@Service
public class AreaServiceImpl implements AreaService {

    // Service 层依赖与 Dao 层

    @Autowired
    private AreaDao areaDao;

    @Override
    public List<Area> getAreaList() {
        return areaDao.queryArea();
    }
}
