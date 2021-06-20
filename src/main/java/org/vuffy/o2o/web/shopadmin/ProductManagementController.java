package org.vuffy.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ProductStateEnum;
import org.vuffy.o2o.exceptions.ProductOperationException;
import org.vuffy.o2o.service.ProductService;
import org.vuffy.o2o.util.CodeUtil;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/18 7:38 上午
 */
@Controller
@RequestMapping(value = "/shopadmin")
public class ProductManagementController {
    @Autowired
    private ProductService productService;

    // 支持上传商品详情图的最大数量
    private static final int MAX_IMAGE_COUNT = 6;

    @RequestMapping(value = "/addproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addProduct(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 1.验证码校验
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "验证码输入错误");
            return modelMap;
        }
        // 2.实体类的初始化，这些变量接收从前端传入的商品、缩略图、详情图片信息
        ObjectMapper mapper = new ObjectMapper();
        Product product = null;
        // 将 json 转为 String，获取商品信息
        String productStr = HttpServletRequestUtil.getString(request, "productStr");
        // 处理文件流
        MultipartHttpServletRequest multipartRequest = null;
        ImageHolder thumbnail = null;
        List<ImageHolder> productImgList = new ArrayList<>();
        // 从 session 中获取文件流
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        try {
            // 3.取出请求中的文件(若有，包括缩略图、详情图)
            if (multipartResolver.isMultipart(request)) {
                multipartRequest = (MultipartHttpServletRequest) request;
                // 取出缩略图、构建 ImageJHolder 对象
                CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
                thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
                // 取出详情图片、构建 List<ImageJHolder> 列表对象，最多 6 张
                for (int i = 0; i < MAX_IMAGE_COUNT; i++) {
                    CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);
                    if (productImgFile != null) {
                        ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                        productImgList.add(productImg);
                    } else {
                        // 没有第 i(i<6) 张图片
                        break;
                    }
                }

            } else {
                modelMap.put("success", false);
                modelMap.put("errorMessage", "上传图片不能为空");
                return modelMap;
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", e.toString());
            return modelMap;
        }

        // 4.获取从前端传入的表单 string 流、将其转换为实体类 Product
        try {
            // ObjectMapper mapper = new ObjectMapper();
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", false);
            return modelMap;
        }
        // Product、缩略图、详情图片均不为空
        if (product != null && thumbnail != null && productImgList.size() > 0) {
            try {
                // 从 session 中获取当前店铺 shopId，并赋值给 product，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                Shop shop = new Shop();
                shop.setShopId(currentShop.getShopId());
                product.setShop(shop);
                // 执行商品添加操作
                ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errorMessage", pe.getStateInfo());
                }

            } catch (ProductOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "请补充商品信息");
        }
        return modelMap;
    }

}
