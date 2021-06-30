package org.vuffy.o2o.web.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 前端页面路由
 */
@Controller
@RequestMapping("/frontend")
public class FrontendController {

    /**
     * 首页路由
     *
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    private String index() {
        return "frontend/index";
    }

    /**
     * 返回查询后的店铺列表
     *
     * @return
     */
    @RequestMapping(value = "/shoplist", method = RequestMethod.GET)
    private String shopList() {
        return "frontend/shoplist";
    }

    /**
     * 店铺详情
     *
     * @return
     */
    @RequestMapping(value = "/shopdetail", method = RequestMethod.GET)
    private String shopDetail() {
        return "frontend/shopdetail";
    }

    /**
     * 商品详情页的路由
     *
     * @return
     */
    @RequestMapping(value = "/productdetail", method = RequestMethod.GET)
    private String productDetail() {
        return "frontend/productdetail";
    }
}
