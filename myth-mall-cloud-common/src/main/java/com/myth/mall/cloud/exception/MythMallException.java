

package com.myth.mall.cloud.exception;

public class MythMallException extends RuntimeException {

    public MythMallException() {
    }

    public MythMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new MythMallException(message);
    }

}
