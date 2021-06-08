package org.vuffy.o2o.exceptions;

/**
 * @author vuffy
 * @version 1.0
 * @description: 有关SHOP操作相关的封装异常
 * @date 2021/5/15 2:09 下午
 */
public class ShopOperationException extends RuntimeException {

    // serivalVersionUID


    // errorMessage 方法接收的描述异常的信息
    public ShopOperationException(String errorMessage) {
        super(errorMessage);
    }
}
