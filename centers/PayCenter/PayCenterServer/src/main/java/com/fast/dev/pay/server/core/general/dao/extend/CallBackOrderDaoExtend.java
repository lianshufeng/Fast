package com.fast.dev.pay.server.core.general.dao.extend;

import java.util.Map;

public interface CallBackOrderDaoExtend {


    /**
     * 增加回调信息
     *
     * @param orderId
     * @param item
     */
    public String add(String orderId, Map<String, Object> item);


}
