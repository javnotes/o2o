package org.vuffy.o2o.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/5/23 7:15 下午
 */
public class CodeUtil {

  public static boolean checkVerifyCode(HttpServletRequest request) {

    String expectedVerifyCode =
        (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
    String actualVerifyCode = HttpServletRequestUtil.getString(request, "actualVerifyCode");

    if (actualVerifyCode == null || !actualVerifyCode.equalsIgnoreCase((expectedVerifyCode))) {
      return false;
    }
    return true;
  }
}
