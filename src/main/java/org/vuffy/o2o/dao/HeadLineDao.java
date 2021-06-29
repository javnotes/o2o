package org.vuffy.o2o.dao;

import org.apache.ibatis.annotations.Param;
import org.vuffy.o2o.entity.HeadLine;

import java.util.List;

public interface HeadLineDao {
    /**
     * 根据传入的查询条件查询头条
     * @param headLineCondition
     * @return
     */
    List<HeadLine> queryHeadLine(@Param("headLineCondition") HeadLine headLineCondition);
}
