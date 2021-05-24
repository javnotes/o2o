package org.vuffy.o2o.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.exceptions.ShopOperationException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

// 工具类，封装 thumbnailator 方法
public class ImageUtil {

    private static String basePath = Thread.currentThread().getContextClassLoader()
            .getResource("").getPath();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Random RANDOM = new Random();

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 将 CommonsMultipartFile 转换为 File
     *
     * @param commonsMultipartFile
     * @return
     */

    public static File CommonsMultipartFileToFile(CommonsMultipartFile commonsMultipartFile) {
        File newFile = new File(commonsMultipartFile.getOriginalFilename());
        try {
            commonsMultipartFile.transferTo(newFile);
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 处理缩略图，并返回新生成相对路径
     * Spring 自带的文件处理对象 CommonsMultipartFile
     *
     * @param fileName 图片文件流
     * @param targetAddr
     * @return
     */
    public static String generateThumbnail(InputStream thumbnailInputStream, String fileName, String targetAddr) {
        String realFileName = getRandomFileName();
        String extension = getFileExtension(fileName);
        makeDirPath(targetAddr);
        // 相对路径，方便系统迁移，包含文件名
        String relativeAddr = targetAddr + realFileName + extension;
        logger.debug("relativeAddr is :" + relativeAddr);

        // 绝对路径
        File dest = new File(PathUtil.getImageBasePath() + relativeAddr);
        logger.debug("dest is :" + PathUtil.getImageBasePath() + relativeAddr);

        try {
            Thumbnails.of(thumbnailInputStream)
                    .size(200, 200)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath
                            + "/watermark.jpg")), 0.2f)
                    .outputQuality(0.8f)
                    .toFile(dest);
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
            throw new ShopOperationException("方法 generateThumbnail 产生异常：" + e.getMessage());
        }
        return relativeAddr;
    }

    /**
     * 创建目标路径所涉及到的目录，即/image/o2o/xxx.jpg, 那么 iamge o2o
     * 这三个文件夹都得自动创建
     *
     * @param targetAddr
     */
    public static void makeDirPath(String targetAddr) {
        String realFileParentPath = PathUtil.getImageBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    /**
     * 获取输入文件流的扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 生成随机文件名，当前年月日小时分钟秒钟+五位随机数
     *
     * @return
     */
    public static String getRandomFileName() {
        // 获取随机的五位数
        int random = RANDOM.nextInt(89999) + 10000;

        String nowTimeStr = SIMPLE_DATE_FORMAT.format(new Date());

        return nowTimeStr + random;
    }


//    public static void main(String[] args) throws IOException {
//        String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        Thumbnails.of(new File("/Users/liliansong/Documents/woman-5584374_1920.jpg"))
//                .size(200, 200)
//                .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f)
//                .outputQuality(0.8f)
//                .toFile("/Users/liliansong/Documents/new-1.jpg");
//
//    }
}
