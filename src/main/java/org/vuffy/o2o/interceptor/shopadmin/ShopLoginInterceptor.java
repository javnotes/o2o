package org.vuffy.o2o.interceptor.shopadmin;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.vuffy.o2o.entity.PersonInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 店铺管理系统登录验证拦截器
 *
 * @author vuffy
 * @create 2021-07-13 15:16
 */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {

    /**
     * 主要做事前拦截，即用户操作发生前，改写preHandle里的逻辑，进行拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 尝试从 session 中取出用户信息表
        Object userObj = request.getSession().getAttribute("user");
        if (userObj != null) {
            // 若用户信息不为空则将 session 里的用户信息转换成 PersonInfo 实体类的对象
            PersonInfo user = (PersonInfo) userObj;
            // 做空值判断，确保 userId 不为空、该帐号的可用状态为1、用户类型为商家
            if (user != null && user.getUserId() != null && user.getUserId() > 0 && user.getEnableStatus() == 1) {
                // 若通过验证则返回true,拦截器返回true之后，用户接下来的操作得以正常执行
                return true;
            }
        }
        // 若不满足登录验证，则直接跳转到帐号登录页面
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<html>");
        printWriter.println("<script>");
        printWriter.println("window.open('" + request.getContextPath() + "/local/login?usertype=2','_self')");
        printWriter.println("</script>");
        printWriter.println("</html>");
        return false;
    }
}
