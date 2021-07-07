package org.vuffy.o2o.util.wechat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信请求校验工具类
 */
public class SignUtil {

    // 与接口配置信息中的Token要一致
    private static String token = "myo2o";

    /**
     * 验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[]{token, timestamp, nonce};
        // 对参数 token, timestamp, nonce 进行字典排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        // 将参数 token, timestamp, nonce 拼接为一个字符串
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
//        for (String s : arr) {
//            content.append(s);
//        }

        // package java.security;
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 得到由参数 token, timestamp, nonce 拼接成的字符串的字节数组
            // 对字节数组进行【加密】
            byte[] digest = md.digest(content.toString().getBytes());
            // 将加密后的字节数组转为十六进制的字符串
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将十六进制的加密字符串与参数 signature 进行对比，标识请求源于微信
        // tmpStr != null：返回 true 说明程序接受到了微信传来的参数timestamp, nonce，其中 token 是程序中已有的
        // 若请求源于微信，则  tmpStr.equals(signature.toUpperCase()) = true
        // 三目运算 c = a > b ? a:b;
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;

    }

    /**
     * 将字节数组转换为十六进制字符串，转换操作实际由方法 byteToHexStr 处理
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
