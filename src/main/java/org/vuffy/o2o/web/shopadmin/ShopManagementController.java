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
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.enums.ShopStateEnum;
import org.vuffy.o2o.exceptions.ShopOperationException;
import org.vuffy.o2o.service.AreaService;
import org.vuffy.o2o.service.ShopCategoryService;
import org.vuffy.o2o.service.ShopService;
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
 * @description: 店铺管理
 * @date 2021/5/15 3:10 下午
 */
@Controller
@RequestMapping("/shopadmin")
// shopadmin：店家管理
public class ShopManagementController {

  @Autowired private ShopService shopService;

  @Autowired private ShopCategoryService shopCategoryService;

  @Autowired private AreaService areaService;

  // 对应 shop/shopoperation.js 中的 initUrl
  @RequestMapping(value = "getshopinitinfo", method = RequestMethod.GET)
  @ResponseBody
  private Map<String, Object> getShopInitInfo() {
    Map<String, Object> modleMap = new HashMap<>();

    List<ShopCategory> shopCategoryList = new ArrayList<>();
    List<Area> areaList = new ArrayList<>();

    try {
      shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
      areaList = areaService.getAreaList();
      modleMap.put("shopCategoryList", shopCategoryList);
      modleMap.put("areaList", areaList);
      modleMap.put("success", true);
    } catch (Exception e) {
      modleMap.put("success", false);
      modleMap.put("errorMessage", e.getMessage());
    }
    return modleMap;
  }

  /**
   * 店铺的注册
   *
   * <p>2.注册店铺 3.返回结果
   *
   * @param: [request] 前端传过来的客户端请求，此对象含客户端请求的所有信息
   * @return: java.util.Map<java.lang.String, java.lang.Object>
   * @author vuffy
   * @date: 2021/5/15 3:13 下午
   */
  @RequestMapping(value = "registershop", method = RequestMethod.POST)
  @ResponseBody
  private Map<String, Object> registerShop(HttpServletRequest request) {

    Map<String, Object> modelMap = new HashMap<>();

    if (!CodeUtil.checkVerifyCode(request)){
      modelMap.put("success", false);
      modelMap.put("errorMessage", "输入了错误的验证码");
      return modelMap;
    }

    // 1.接受并转换相应的参数：店铺信息、图片信息，工具类：HttpServletRequestUtil
    // shopStr 从前端传过来的店铺信息
    String shopStr = HttpServletRequestUtil.getString(request, "shopStr");

    // jackson
    ObjectMapper mapper = new ObjectMapper();
    Shop shop = null;
    try {
      // 将前端传递过来的字符串shopStr转换成实体类shop
      shop = mapper.readValue(shopStr, Shop.class);
    } catch (Exception e) {
      // JsonMappingException JsonProcessingException
      e.printStackTrace();
      // 返回前台：失败
      modelMap.put("success", false);
      // 失败原因
      modelMap.put("errorMessage", e.getMessage());
      return modelMap;
    }

    // 图片相关
    // 接收图片：Spring自带CommonsMultipartFile，提取文件流
    CommonsMultipartFile shopImg = null;
    // 文件上传解析器,解析request中的文件信息
    // request.getSession().getServletContext()：request的本次会话中的上下文
    CommonsMultipartResolver commonsMultipartResolver =
        new CommonsMultipartResolver(request.getSession().getServletContext());

    // 通过文件上传解析器，来判断request中是否有上传的文件流
    if (commonsMultipartResolver.isMultipart(request)) {
      // 有文件流，则将HttpServletRequest转化为MultipartHttpServletRequest
      MultipartHttpServletRequest multipartHttpServletRequest =
          (MultipartHttpServletRequest) request;
      // shopImg：前端穿过来的,shopImg 是指 文件上传标签的 name=值
      // 根据 name 获取上传的文件...
      shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
    } else {
      // 规定必须上传图片
      modelMap.put("success", false);
      modelMap.put("errorMessage", "请上传图片");
      return modelMap;
    }

    // 2. 注册店铺
    if (shop != null && shopImg != null) {
      // 从Session中获取店主信息
      PersonInfo owner = new PersonInfo();
      // Session TODO
      owner.setUserId(1L);
      shop.setPersonInfo(owner);

      // 上文中，获取到的shopImg为CommonsMultipartFile类型
      // 而shopService.addShop()中shopImg为File类型，可便于Service层的单元测试
      // 故需进行转换，借助方法 inputStreamToFile

      // 路径
      //            File shopImgFile = new File(PathUtil.getImageBasePath() +
      // ImageUtil.getRandomFileName());
      //            try {
      //                shopImgFile.createNewFile();
      //            } catch (IOException e) {
      //                modelMap.put("success", false);
      //                modelMap.put("errorMessage", e.getMessage());
      //                return modelMap;
      //            }
      //            try {
      //                // CommonsMultipartFile shopImg，返回 InputStream
      //                inputStreamToFile(shopImg.getInputStream(), shopImgFile);
      //            } catch (IOException e) {
      //                modelMap.put("success", false);
      //                modelMap.put("errorMessage", e.getMessage());
      //                return modelMap;
      //            }

      // 执行注册
      ShopExecution shopExecution = null;
      try {
        shopExecution =
            shopService.addShop(shop, shopImg.getInputStream(), shopImg.getOriginalFilename());
        if (shopExecution.getState() == ShopStateEnum.CHECK.getState()) {
          modelMap.put("success", true);
        }
      } catch (ShopOperationException e) {
        modelMap.put("success", false);
        modelMap.put("errorMessage", e.getMessage());
      } catch (IOException e) {
        modelMap.put("success", false);
        modelMap.put("errorMessage", e.getMessage());
      }
      return modelMap;
    } else {
      modelMap.put("success", false);
      modelMap.put("errorMessage", "请填写店铺信息");
      return modelMap;
    }
  }

  /**
   * 将CommonsMultipartFile类型的shopImg，转为File类型
   *
   * @param: [inputStream, file]
   * @return: void
   * @author vuffy
   * @date: 2021/5/15 6:56 下午
   */
  //    private static void inputStreamToFile(InputStream inputStream, File file) {
  //        FileOutputStream outputStream = null;
  //        try {
  //            outputStream = new FileOutputStream(file);
  //
  //            // bytesRead = inputStream.read(byte[] b)：返回的是读到的字节个数
  //            int bytesRead = 0;
  //
  //            // 缓存数组
  //            byte[] buffer = new byte[1024];
  //
  //            // 读到的字节放在了bytes字节数组里，读到末尾没数据了返回-1
  //            while ((bytesRead = inputStream.read(buffer)) != -1) {
  //                // 读到的字节写入到输出流，保存至 file
  //                outputStream.write(buffer, 0, bytesRead);
  //            }
  //        } catch (Exception e) {
  //            throw new RuntimeException("调用方法 inputStreamToFile 产生异常：" + e.getMessage());
  //        } finally {
  //            try {
  //                // 不关闭，会产生溢出，
  //                //垃圾回收器只能回收java程序产生的那些类实例对象，不能回收系统产生的资源(如：流)，
  //                // 因此要本身调用close()方法释放资。
  //                if (outputStream != null) {
  //                    outputStream.close();
  //                }
  //                if (inputStream != null) {
  //                    inputStream.close();
  //                }
  //            } catch (IOException e) {
  //                throw new RuntimeException("方法 inputStreamToFile 产生 IO close() 异常：" +
  // e.getMessage());
  //            }
  //        }
  //    }
}
