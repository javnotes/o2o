package org.vuffy.o2o.service;

import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Shop;

import java.io.InputStream;

public interface ShopService {
    // fileName 文件名, InputStream 无法获取文件名
    // 重构前： ShopExecution addShop(Shop shop, File file); // 可以直接传入 InputStream
    ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName);
}
