package org.vuffy.o2o.exceptions;

import org.vuffy.o2o.entity.ShopCategory;

public class ShopCategoryOperationException extends RuntimeException {

    public ShopCategoryOperationException(String msg) {
        super(msg);
    }
}
