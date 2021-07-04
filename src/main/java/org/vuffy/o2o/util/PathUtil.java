package org.vuffy.o2o.util;

import java.util.Locale;

public class PathUtil {

    private static String seperator = System.getProperty("file.separator");

    public static String getImageBasePath() {
        String os = System.getProperty("os.name");
        String bathPath = "";

        if (os.toLowerCase().startsWith("win")) {
            bathPath = "C:/documents/image";
        } else {
            // Mac 路径
//            bathPath = "/Users/liliansong/Documents/image";
            // 阿里云 CentOs lujing
            bathPath = "/home/o2o/image/upload";
        }
        bathPath = bathPath.replace("/", seperator);
        return bathPath;
    }

    public static String getShopImagePath(long shopId) {
        String imagePath = "/upload/item/shop/" + shopId + "/";
        return imagePath.replace("/", seperator);

    }
}
