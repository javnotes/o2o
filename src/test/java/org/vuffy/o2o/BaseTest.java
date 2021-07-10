package org.vuffy.o2o;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 用来配置 Spring 和 Junit 整合，Junit 启动时加载 Spring IOC 容器
 */

@RunWith(SpringJUnit4ClassRunner.class)

//告诉Junit，Spring 的配置文件在哪
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml", "classpath:spring/spring-redis.xml"})
public class BaseTest {
}
