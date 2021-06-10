package org.vuffy.o2o.dto;

import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ShopStateEnum;

import java.util.List;

public class ShopExecution {
    //  结果状态代码
    private int state;

    // 结果状态说明
    private String sateInfo;

    // 店铺数量
    private int count;

    // 操作的 shop(对其进行增删改)
    private Shop shop;

    // shop 列表（查询店铺列表时）
    private List<Shop> shopList;

    public ShopExecution() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSateInfo() {
        return sateInfo;
    }

    public void setSateInfo(String sateInfo) {
        this.sateInfo = sateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Shop> getShopList() {
        return shopList;
    }

    public void setShopList(List<Shop> shopList) {
        this.shopList = shopList;
    }

    // 店铺操作失败的时候使用的构造器，仅处理状态
    public ShopExecution(ShopStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.sateInfo = stateEnum.getStateInfo();
    }

    // 店铺操作成功的时候使用的构造器，返回单个 shop
    public ShopExecution(ShopStateEnum stateEnum, Shop shop) {
        this.state = stateEnum.getState();
        this.sateInfo = stateEnum.getStateInfo();
        this.shop = shop;
    }

    // 店铺操作成功的时候使用的构造器，返回多个 shop
    public ShopExecution(ShopStateEnum stateEnum, List<Shop> shopList) {
        this.state = stateEnum.getState();
        this.sateInfo = stateEnum.getStateInfo();
        this.shopList = shopList;
    }
}
