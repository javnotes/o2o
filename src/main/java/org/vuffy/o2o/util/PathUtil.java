package org.vuffy.o2o.util;

public class PathUtil {

    private static String seperator = System.getProperty("file.separator");

    public static String getImageBasePath() {
        String os = System.getProperty("os.name");
        String bathPath = "";

        if (os.toLowerCase().startsWith("win")) {
            bathPath = "C:/documents/image";
        } else {
            // Mac 路径
//           bathPath = "/Users/liliansong/Documents/image";
            // 在阿里云中，需先创建该路径
            bathPath = "/home/o2o/image";
        }
        bathPath = bathPath.replace("/", seperator);
        return bathPath;
    }

    public static String getShopImagePath(long shopId) {
        String imagePath = "/upload/images/item/shop/" + shopId + "/";
        return imagePath.replace("/", seperator);
    }

    public static String getHeadLinePath(long shopId) {
        String imagePath = "/upload/images/item/headtittle/" + shopId + "/";
        return imagePath.replace("/", seperator);
    }

    public static String getShopCategoryPath(long shopId) {
        String imagePath = "/upload/images/item/shopcategory/" + shopId + "/";
        return imagePath.replace("/", seperator);
    }
}