package org.vuffy.o2o.web.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.dao.ProductDao;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.service.ProductService;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    // 根据商品Id获取商品的信息
    @RequestMapping(value = "/listproductdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductDetailPageInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取前台传入的productId
        long productId = HttpServletRequestUtil.getLong(request, "productId");
        Product product = null;

        if (productId != -1) {
            // 得到商品信息，包括商品的详情图片
            product = productService.getProductById(productId);
            modelMap.put("product", product);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "empty productId");
        }
        return modelMap;
    }
}
