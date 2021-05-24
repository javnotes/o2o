package org.vuffy.o2o.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vuffy
 * @version 1.0
 * @description: 处理 HttpServletRequest
 * @date 2021/5/15 3:25 下午
 */
public class HttpServletRequestUtil {

    /**
     * HttpServletRequest对象中，有各种key
     * 从request中提取一个key，返回值为Integer
     * @param: [request, key]
     * @return: int
     * @author vuffy
     * @date: 2021/5/15 3:46 下午
     */
    public static Integer getInteger(HttpServletRequest request, String key) {
        try {
            return Integer.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1;
        }
    }

    public static Long getLong(HttpServletRequest request, String key) {
        try {
            return Long.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1L;
        }
    }

    public static Double getDouble(HttpServletRequest request, String key) {
        try {
            return Double.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1d;
        }
    }

    public static boolean getBoolean(HttpServletRequest request, String key) {
        try {
            return Boolean.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return false;
        }
    }

    public static String getString(HttpServletRequest request, String key) {
        try {
            String result = request.getParameter(key);
            if (result != null) {
                result = result.trim();
            }
            if ("".equals(result)) {
                result = null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
