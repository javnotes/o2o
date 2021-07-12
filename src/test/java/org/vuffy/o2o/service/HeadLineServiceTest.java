package org.vuffy.o2o.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.HeadLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadLineServiceTest extends BaseTest {

    @Autowired
    private HeadLineService headLineService;

    @Test
    public void testGetHeadLineList() throws IOException {
        HeadLine headLine = new HeadLine();
        headLine.setEnableStatus(1);
        List<HeadLine> headLineList = headLineService.getHeadLineList(headLine);
        System.out.println(headLineList.size());
    }
}
