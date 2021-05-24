package org.vuffy.o2o.util;

import java.util.Locale;

public class PathUtil {

    private static String seperator = System.getProperty("file.separator");

    public static String getImageBasePath() {
        String os = System.getProperty("os.name");
        String bathPath = "";

        if (os.toLowerCase().startsWith("win")) {
            bathPath = "C:/documents/image/";
        } else {
            bathPath = "/Users/liliansong/Documents/image/";
        }
        bathPath = bathPath.replace("/", seperator);
        return bathPath;
    }

    public static String getShopImagePath(long shopId) {
        String imagePath = "upload/item/shop/" + shopId + "/";
        return imagePath.replace("/", seperator);

    }
}
