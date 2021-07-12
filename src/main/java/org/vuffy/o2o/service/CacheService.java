package org.vuffy.o2o.service;

public interface CacheService {
    /**
     * 依据 key 前缀删除匹配该模式下的所有 key-value
     * 如传入:shopcategory,则shopcategory_allfirstlevel 等以
     * shopcategory 打头的 key_value 都会被清空
     *
     * @param keyPrefix
     */
    void removeFromCache(String keyPrefix);
}
