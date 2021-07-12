package org.vuffy.o2o.web.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.dto.LocalAuthExecution;
import org.vuffy.o2o.entity.LocalAuth;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.enums.LocalAuthStateEnum;
import org.vuffy.o2o.exceptions.LocalAuthOperationException;
import org.vuffy.o2o.service.LocalAuthService;
import org.vuffy.o2o.util.CodeUtil;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/local", method = {RequestMethod.GET, RequestMethod.POST})
public class LocalAuthController {

    @Autowired
    private LocalAuthService localAuthService;

    @RequestMapping(value = "bindlocalauth", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> bindLocalAuth(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 校验验证码
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "验证码输入错误");
            return modelMap;
        }

        // 获取输入的账号、密码
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");

        // 从 session 中获取当前用户的信息（用户一旦通过微信登录后，便能获取到用户的信息）
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        // 非空判断，要求帐号密码以及当前的用户session非空
        if (username != null && password != null && user != null && user.getUserId() != null) {
            // 创建 LocalAuth 对象，以前端传入的账号名、密码进行赋值
            LocalAuth localAuth = new LocalAuth();
            localAuth.setUsername(username);
            localAuth.setPassword(password);
            localAuth.setPersonInfo(user);
            // 绑定账号
            LocalAuthExecution localAuthExecution = localAuthService.bindLocalAuth(localAuth);
            if (localAuthExecution.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
            } else {
                modelMap.put("success", false);
                modelMap.put("errorMessage", localAuthExecution.getStateInfo());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "用户名、密码不能为空");
        }
        return modelMap;
    }

    @RequestMapping(value = "/changelocalpwd", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> changeLocalPwd(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();

        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "验证码输入有误");
            return modelMap;
        }

        // 获取账号、原密码、新密码
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");
        String newPassword = HttpServletRequestUtil.getString(request, "newPassword");

        // 在 Session 中获取当前用户信息（用户信息来自于微信登录后微信方发送的微信账号信息）
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");

        // 账号名、旧密码、新密码、当前用户session均不为空，新、旧密码不同
        if (username != null && password != null && newPassword != null && user != null &&
                user.getUserId() != null && !password.equals(newPassword)) {
            try {
                // 输入的账号名与原账号名进行比较，不一致则退出
                LocalAuth localAuth = localAuthService.getLocalAuthByUserId(user.getUserId());
                if (localAuth == null || !localAuth.getUsername().equals(username)) {
                    modelMap.put("success", false);
                    modelMap.put("errorMessage", "输入的账号名与登录账号名不一致");
                    return modelMap;
                }
                // 修改账号名的密码
                LocalAuthExecution localAuthExecution = localAuthService.modifyLocalAuth(user.getUserId(), username, password, newPassword);
                if (localAuthExecution.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errorMessge", localAuthExecution.getStateInfo());
                }
            } catch (LocalAuthOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.getMessage());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "输入信息不正确（注意：新、旧密码需不同）");
        }
        return modelMap;
    }

    @RequestMapping(value = "/logincheck", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> logincheck(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 判断是否需要校验验证码
        boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
        if (needVerify && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "验证码输入错误");
            return modelMap;
        }

        // 获取输入的账号名、密码
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");


        if (username != null && password != null) {
            LocalAuth localAuth = localAuthService.getLocalAuthByUsernameAndPwd(username, password);
            if (localAuth != null) {
                // 能够获取到账号信息，即可登录成功，便在Session中设置用户信息
                modelMap.put("success", true);
                request.getSession().setAttribute("user", localAuth.getPersonInfo());
            } else {
                modelMap.put("success", false);
                modelMap.put("errorMessage", "账号名或密码错误");
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "用户名、密码不能为空");
        }
        return modelMap;
    }

    /**
     * 用户点击退出按钮时，注销 Session
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        request.getSession().setAttribute("user", null);
        modelMap.put("success", true);
        return modelMap;
    }
}