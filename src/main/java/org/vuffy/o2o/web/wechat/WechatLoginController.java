package org.vuffy.o2o.web.wechat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.vuffy.o2o.dto.UserAccessToken;
import org.vuffy.o2o.dto.WechatUser;
import org.vuffy.o2o.util.wechat.WechatUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户访问到此路由后，跳转至微信，
 * 获取关注公众号之后的微信用户信息的接口，如果在微信中访问
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx91a45ae270e8fdc1&redirect_uri=http://o2o.vuffy.cn/o2o/wechatlogin/logincheck&role_type=1&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect
 * 则这里将会获取到code(key-value),之后再可以通过code获取到access_token(openId) 进而获取到用户信息
 * openId：微信用户对公众号的唯一ID，故使用 openId 来识别、标识用户
 */
@Controller
@RequestMapping("wechatlogin")
public class WechatLoginController {

    private static Logger logger = LoggerFactory.getLogger(WechatController.class);

//    private static final String FORTEND = "1";
//    private static final String SHOPEND = "2";

    //    @Autowired
//    private PersionInfoService persionInfoService;
//
//    @Autowired
//    private WechatAuthService wechatAuthService;
//
    @RequestMapping(value = "/logincheck", method = {RequestMethod.GET})
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("wexin login get...");
        // 获取微信方传输过来的code，通过code可获取access_token,进而获取用户信息
        String code = request.getParameter("code");
        // 这个state可以用来传我们自定义的信息，方便程序调用，这里也可以不用
        String roleType = request.getParameter("state");
        logger.debug("weixin login code:" + code);
        WechatUser wechatUser = null;
        String openId = null;
//        WechatAuth wechatAuth = null;
        if (null != code) {
            UserAccessToken token;
            try {
                // 通过code访问链接，获取accesstToken
                token = WechatUtil.getUserAccessToken(code);
                logger.debug("weixin login token:" + token.toString());

                // 通过 token 获取 accessToken
                String accessToken = token.getAccessToken();
                // 通过 token 获取 openId
                openId = token.getOpenId();
                // 通过 accessToken 和 openId 获取用户昵称等信息
                wechatUser = WechatUtil.getUserInfo(accessToken, openId);
                logger.debug("Weixin login user:" + wechatUser.toString());
                request.getSession().setAttribute("openId", openId);
            } catch (IOException e) {
                logger.error("Error in getUserAccessToken or getUserInfo or findByOpenId:" + e.toString());
                e.printStackTrace();
            }
        }

        if (wechatUser != null) {
            // 获取到微信的验证消息后，返回到指定的路由，该路由需自己设定
            return "frontend/index";
        } else {
            return null;
        }
    }
}
