package org.vuffy.o2o.exceptions;

/**
 * @author vuffy
 * @version 1.0
 * @description: 有关 ProductCategory 操作相关的封装异常
 * @date 2021/5/15 2:09 下午
 */
public class ProductCategoryOperationException extends RuntimeException {

    // errorMessage 方法接收的描述异常的信息
    public ProductCategoryOperationException(String errorMessage) {
        super(errorMessage);
    }
}
