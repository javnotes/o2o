package org.vuffy.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ProductStateEnum;
import org.vuffy.o2o.exceptions.ProductOperationException;
import org.vuffy.o2o.service.ProductCategoryService;
import org.vuffy.o2o.service.ProductService;
import org.vuffy.o2o.util.CodeUtil;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    @Autowired
    private ProductCategoryService productCategoryService;

    // 支持上传商品详情图的最大数量
    private static final int MAX_IMAGE_COUNT = 6;

    /**
     * 通过店铺id获取该店铺下的商品列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getproductlistbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getProductListByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取前台传入的页码
        int pageIndex = HttpServletRequestUtil.getInteger(request, "pageIndex");
        // 获取前台传入的每页可显示商品个数的上限
        int pageSize = HttpServletRequestUtil.getInteger(request, "pageSize");
        // 从当前session中获取店铺信息（shopId）
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        // 空值判断
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
            // 获取传入的检索条件：包括从某个商品类别、模糊商品名去筛选某个店铺下的商品列表
            // 筛选的条件可以进行排列组合
            long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
            String productName = HttpServletRequestUtil.getString(request, "productName");
            Product productCondition = compactProductCondition(currentShop.getShopId(), productCategoryId, productName);
            // 传入查询条件以及分页信息，返回商品列表及个数
            ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "pageSize、pageIndex或shopId 不能为空");
        }
        return modelMap;
    }

    /**
     * 封装商品查询条件到Product实例中
     *
     * @param shopId
     * @param productCategoryId
     * @param productName
     * @return
     */
    private Product compactProductCondition(long shopId, long productCategoryId, String productName) {
        Product productCondition = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        // 若有指定的商品类别
        if (productCategoryId != -1L) {
            ProductCategory pc = new ProductCategory();
            pc.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(pc);
        }
        // 若有商品名（模糊）
        if (productName != null) {
            productCondition.setProductName(productName);
        }
        return productCondition;
    }

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
                thumbnail = handleImage(request, thumbnail, productImgList);
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
                product.setShop(currentShop);
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

    /**
     * 通过商品 id 获取商品信息
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
    @ResponseBody
//     @RequestParam 的参数其实就是从HttpServletRequest里去获取的，spring封装了相关的功能，遇到后会解析
    private Map<String, Object> getProductById(@RequestParam Long productId) {
        Map<String, Object> modelMap = new HashMap<>();
        // 非空判断
        if (productId > -1) {
            // 获取商品信息
            Product product = productService.getProductById(productId);
            // 获取该店铺的商品类别列表 这有什么用？如：修改商品时，获取该商品信息中就已包含了该店铺的所有商品类别信息
            List<ProductCategory> productCategories = productCategoryService.getProductCategoryList(product.getShop().getShopId());
            modelMap.put("product", product);
            modelMap.put("productCategoryList", productCategories);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "empty productId");
        }
        return modelMap;
    }

    /**
     * 商品编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyProduct(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 判断：商品编辑(需校验验证码) or 仅商品的上、下架(直接生效，无需验证码)
        // statusChange=true 对应商品的上、下架
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
        // 验证码的校验
        if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "验证码输入错误");
            return modelMap;
        }
        // 接收来自前端的参数，先初始化：商品、缩略图、详情图
        ObjectMapper mapper = new ObjectMapper();
        Product product = null;
        ImageHolder thumbnail = null;
        List<ImageHolder> productImgList = new ArrayList<>();
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getServletContext());
        // 若请求中存在文件流，则取出(缩略图、详情图)，可以不更新图片
        try {
            if (multipartResolver.isMultipart(request)) {
                thumbnail = handleImage(request, thumbnail, productImgList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String productStr = HttpServletRequestUtil.getString(request, "productStr");
            // 获取前端传来的表单string流，将其转换为Product类
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", e.toString());
            return modelMap;
        }
        // 非空判断
        if (product != null) {
            try {
                // 从session 中获取当前店铺id，并将id赋给product，这样是为了减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                product.setShop(currentShop);
                // 修改商品信息
                ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);
                // 商品修改是否成功
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errorMessage", pe.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "商品信息不能为空");
        }
        return modelMap;
    }

    private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail, List<ImageHolder> productImgList) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 取出缩略图，构建 ImageHolder 对象 thumbnail
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
        if (thumbnailFile != null) {
            thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        }
        // 取出详情图，构建 List<ImageHolder> 列表对象，最多6张,productImgList
        for (int i = 0; i < MAX_IMAGE_COUNT; i++) {
            CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);
            if (productImgFile != null) {
                // 取出的第 i 张详情图不为空，则将其加入至详情图列表
                ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                productImgList.add(productImg);
            } else {
                // 取出的第 i 张详情图为空
                break;
            }
        }
        return thumbnail;
    }
}
