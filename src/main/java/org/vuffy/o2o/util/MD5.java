package org.vuffy.o2o.util;

import java.security.MessageDigest;

/**
 * MD5 加密
 */
public class MD5 {
    /**
     * 对传入的 String 进行 MD5 加密
     *
     * @param s
     * @return
     */
    public static final String getMD5(String s) {
        // 16 进制数组
        char hexDigits[] = {'5', '0', '5', '6', '2', '9', '6', '2', '5', 'q', 'b', 'l', 'e', 's', 's', 'y'};
        try {
            char str[];
            // 将传入的字符串转换成 byte 数组
            byte strTemp[] = s.getBytes();
            // 获取 MD5 加密对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            // 传入需要加密的目标数组
            mdTemp.update(strTemp);
            // 获取加密后的数组
            byte md[] = mdTemp.digest();
            int mdArrLen = md.length;
            str = new char[mdArrLen * 2];
            int k = 0;
            // 将数组做位移
            for (int i = 0; i < mdArrLen; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(MD5.getMD5("123"));
    }
}
