package org.vuffy.o2o.enums;

public enum ShopStateEnum {
    CHECK(0, "审核中"), OFFLINE(-1, "非法店铺"),
    SUCCESS(1, "操作成功"), PASS(2, "通过认证"),
    INNER_ERROR(-1001, "内部系统错误"), NULL_SHOPID(-1002, "ShopId 为空"),
    NULL_SHOP(-1003, "Shop 为空"), NULL_SHOPCATEGORY(-1004, "ShopCategory 为空"),
    NULL_SHOPAREA(-1005, "ShopArea 为空");

    private int state;
    private String stateInfo;

    ShopStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 依据传入的 state 返回相应的 enum 值
     */
    public static ShopStateEnum stateOf(int state) {
        for (ShopStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
}
