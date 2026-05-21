package com.meishe.mvvm;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/23 上午12:35
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface IBaseModelListener<T> {
    void onLoadSuccess(BaseListMvvmModel model, T data, PagingResult... result);
    void onLoadFail(BaseListMvvmModel model, String message, PagingResult... result);
}
