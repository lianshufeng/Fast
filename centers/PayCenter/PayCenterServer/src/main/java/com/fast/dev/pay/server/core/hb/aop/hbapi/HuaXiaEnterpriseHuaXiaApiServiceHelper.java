package com.fast.dev.pay.server.core.hb.aop.hbapi;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.helper.TransactionHelper;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaApiJournalDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaApiJournal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class HuaXiaEnterpriseHuaXiaApiServiceHelper {

    //当前线程
    private ThreadLocal<Optional<HuaXiaApiJournal>> threadLocal = new ThreadLocal<>();

    @Autowired
    private HuaXiaApiJournalDao huaXiaApiJournalDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    //默认数据过期时间为30天
    private final static long TimeOut = 1000l * 60 * 60 * 24 * 30;


    /**
     * 设置当前实体
     */
    protected void build(String methodName) {
        AtomicReference<Optional<HuaXiaApiJournal>> ret = new AtomicReference<>();
        this.transactionHelper.noTransaction(() -> {
            ret.set(Optional.ofNullable(huaXiaApiJournalDao.create(methodName)));
        });
        threadLocal.set(ret.get());
    }

    /**
     * 删除当前操作的实体,使用当前线程（当前事务)
     */
    protected void finish() {
        this.threadLocal.get().ifPresent((it) -> {
            this.transactionHelper.noTransaction(() -> {
                huaXiaApiJournalDao.updateTTL(it.getId(), this.dbHelper.getTime() + TimeOut);
            });
        });
        this.threadLocal.remove();
    }


    /**
     * 获取交易流水号
     *
     * @return
     */
    public String getTradeNo() {
        AtomicReference<String> ret = new AtomicReference<>();
        threadLocal.get().ifPresent((it) -> {
            ret.set(it.getTradeNo());
        });
        return ret.get();
    }


    /**
     * 获取交易号
     *
     * @return
     */
    public String getPcsDate() {
        AtomicReference<String> ret = new AtomicReference<>();
        threadLocal.get().ifPresent((it) -> {
            ret.set(it.getPcsDate());
        });
        return ret.get();
    }


    /**
     * 更新服务器请求
     *
     * @param request
     */
    public void updateRequest(Object request) {
        threadLocal.get().ifPresent((it) -> {
            this.transactionHelper.noTransaction(() -> {
                huaXiaApiJournalDao.updateRequest(it.getId(), JsonUtil.toJson(request));
            });
        });
    }

    /**
     * 更新服务器应答
     *
     * @param response
     */
    public void updateResponse(Object response) {
        threadLocal.get().ifPresent((it) -> {
            this.transactionHelper.noTransaction(() -> {
                huaXiaApiJournalDao.updateResponse(it.getId(), JsonUtil.toJson(response));
            });
        });
    }

}
