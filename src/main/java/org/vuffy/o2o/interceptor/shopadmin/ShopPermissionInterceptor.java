package org.vuffy.o2o.interceptor.shopadmin;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.vuffy.o2o.entity.Shop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 店铺管理系统操作验证拦截器
 *
 * @author vuffy
 * @create 2021-07-13 15:28
 */
public class ShopPermissionInterceptor extends HandlerInterceptorAdapter {

    /**
     * 主要做事前拦截，即用户操作发生前，改写preHandle里的逻辑，进行用户操作权限的拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 session 中获取当前选择的店铺
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        // 从 session 中获取当前用户可操作的店铺列表
        List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
        if (currentShop != null && shopList != null) {
            // 遍历可操作的店铺
            for (Shop shop : shopList) {
                // 如果当前店铺在可操作的列表里,则返回 true
                if (currentShop.getShopId().equals(shop.getShopId())) {
                    return true;
                }
            }
        }
        // 若不满足拦截器的验证则返回false,终止用户操作的执行
        return false;
    }
}