package com.fast.dev.pay.server.core.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CallBackUtil {


    /**
     * 执行回调方法
     *
     * @param callBack
     * @return
     */
    public static void execute(HttpServletResponse response, CallBack callBack) {
        try {
            callBack.execute();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            response.setStatus(500);
        }
    }


    @FunctionalInterface
    public static interface CallBack {
        public void execute();
    }

}
