package com.meishe.base.model;

/**
 * author lhz
 * description:统一的错误信息
 * Unified error message
 * @param <T> the type parameter
 */
public class NvsError<T> {
    private T error;


    public NvsError(T error) {
        this.error = error;
    }

    /**
     * Gets error.
     *  得到错误
     * @return the error
     */
    public T getError() {
        return error;
    }
}
