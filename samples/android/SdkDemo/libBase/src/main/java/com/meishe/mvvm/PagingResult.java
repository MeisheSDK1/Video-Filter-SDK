package com.meishe.mvvm;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/23 上午12:34
 * @Description : 分页结果
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class PagingResult {
    public PagingResult(boolean isFirstPage, boolean isEmpty, boolean hasNextPage){
        this.isFirstPage = isFirstPage;
        this.isEmpty = isEmpty;
        this.hasNextPage = hasNextPage;
    }

    public boolean isFirstPage;
    public boolean isEmpty;
    public boolean hasNextPage;
}
