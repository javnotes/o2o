package org.vuffy.o2o.service;

import org.vuffy.o2o.entity.Area;

import java.util.List;

public interface AreaService {

    String AREALISTKEY = "arealist";

    List<Area> getAreaList();
}
