package org.vuffy.o2o.util.wechat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vuffy.o2o.dto.UserAccessToken;
import org.vuffy.o2o.dto.WechatUser;
import org.vuffy.o2o.entity.PersonInfo;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;

/**
 * 获取，根据参数 code 获取 UserAccessToken 实例类
 * 用户信息打包存在 AccessToken；使用
 * WechatUtil. 获取
 * WechatUtil. 获取
 */
public class WechatUtil {

    private static Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    public static UserAccessToken getUserAccessToken(String code) throws IOException {
        // 微信方的appId
        String appId = "wx91a45ae270e8fdc1";
        logger.debug("appId:" + appId);
        // 微信方的 appsecret
        String appsecret = "04837890db583f5bdf397c449c05fd4d";
        logger.debug("appsecret:" + appsecret);
        // 根据传入的参数 code,拼接出访问微信定义好的接口的URL
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appsecret
                + "&code=" + code + "&grant_type=authorization_code";
        // 向定义好的URL发送请求获取token json字符串向相应URL发送请求获取token json字符串
        String tokenStr = httpsRequest(url, "GET", null);
        logger.debug("userAccessToken:" + tokenStr);
        UserAccessToken token = new UserAccessToken();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将json字符串转换为相应对象
            token = objectMapper.readValue(tokenStr, UserAccessToken.class);
        } catch (JsonParseException e) {
            logger.error("获取用户 AccessToken 失败：" + e.getMessage());
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error("获取用户 AccessToken 失败：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("获取用户 AccessToken 失败：" + e.getMessage());
            e.printStackTrace();
        }

        if (token == null) {
            logger.error("获取用户 AccessToken 失败，为空值。");
            return null;
        }
        return token;
    }

    /**
     * 发起 https 请求，获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return json 字符串
     */
    private static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {

        StringBuffer buffer = new StringBuffer();

        try {
            // 创建 SSLContext 对象，使用指定的信任管理器进行初始化
            TrustManager[] trustManagers = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            // 从上述 SSLContext 对象中得到 SSLSocketFactory 对象
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(sslSocketFactory);

            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpsURLConnection.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpsURLConnection.connect();
            }

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpsURLConnection.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpsURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            httpsURLConnection.disconnect();
            logger.debug("https buffer:" + buffer.toString());
        } catch (ConnectException ce) {
            logger.error("Weixin server connection timed out.");
        } catch (Exception e) {
            logger.error("https request error: {}", e);
        }
        return buffer.toString();
    }


    /**
     * 获取WechatUser实体类
     *
     * @param accessToken
     * @param openId
     * @return
     */
    public static WechatUser getUserInfo(String accessToken, String openId) {

        // 根据传入的 accessToken、openId 拼接出访问微信定义的端口并获取用户信息的URL
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";

        // 访问该 url 获取用户信息 json 字符串
        String userStr = httpsRequest(url, "GET", null);
        logger.debug("user info:" + userStr);
        WechatUser user = new WechatUser();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 将 json 字符串转换成相应的对象
            user = objectMapper.readValue(userStr, WechatUser.class);
        } catch (JsonParseException e) {
            logger.error("获取用户信息失败：" + e.getMessage());
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error("获取用户信息失败：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("获取用户信息失败：" + e.getMessage());
            e.printStackTrace();
        }

        if (user == null) {
            logger.error("获取用户信息失败。");
            return null;
        }
        return user;
    }

    /**
     * 将微信方的用户的信息转换成PersonInfo里的信息，返回PersonInfo对象
     * @param wechatUser
     * @return
     */
    public static PersonInfo getPersonInfoFromRequest(WechatUser wechatUser) {
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserName(wechatUser.getNickName());
        personInfo.setGender(wechatUser.getSex() + "");
        personInfo.setProfileImg(wechatUser.getHeadimgurl());
        personInfo.setEnableStatus(1);
        return personInfo;
    }
}
