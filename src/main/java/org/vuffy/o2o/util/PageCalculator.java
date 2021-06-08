package org.vuffy.o2o.util;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/5 5:12 下午
 */
public class PageCalculator {
  public static int calculateRowIndex(int pageIndex, int pageSize) {
    return (pageIndex > 0) ? (pageIndex - 1) * pageSize : 0;
  }
}
