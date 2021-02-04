package com.fast.dev.core.helper

import com.fast.dev.core.util.net.apache.HttpClientUtil
import com.fast.dev.core.util.net.apache.HttpModel
import com.fast.dev.core.util.net.apache.MethodType
import com.fast.dev.core.util.net.apache.ResponseModel
import groovy.util.logging.Log
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.ConcurrentHashMap

@Log
public abstract class AHeartbeatHelper {


    //任务集合
    private Set<String> promiseSet = ConcurrentHashMap.newKeySet()


    //定时器
    private Timer timer = new Timer();


    @PostConstruct
    private void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, getSendSleepTime(), getSendSleepTime());
    }


    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        if (this.promiseSet == null || this.promiseSet.size() <= 0) {
            return;
        }

        String ids = String.join(",", this.promiseSet);

        //构建发送的地址
        String host = getHost();
        while (host.substring(host.length() - 1, host.length()).equals("/")) {
            host = host.substring(0, host.length() - 1);
        }
        String url = host + "/promisetask/rpc/api/heartbeat";

        //网络通信
        HttpModel httpModel = HttpModel.builder().method(MethodType.Post).url(url).body("id=" + ids).build();
        ResponseModel responseModel = HttpClientUtil.request(httpModel);
        Object ret = responseModel.getBody();

        // 通信结果是否有效，无效则删除
        if (ret['state'] == 'Success') {
            def content = ret['content']
            for (String it : this.promiseSet) {
                if (content[it] != true) {
                    remove(it)
                }
            }
        }


    }


    /**
     * 增加promise的id
     */
    public void add(String promiseId) {
        if (StringUtils.hasText(promiseId)) {
            this.promiseSet.add(promiseId);
        }
    }

    /**
     * 删除promiose的id
     *
     * @param promiseId
     */
    public void remove(String promiseId) {
        this.promiseSet.remove(promiseId);
    }


    /**
     * promiseid是否存在
     *
     * @param promiseId
     * @return
     */
    public boolean contains(String promiseId) {
        return this.promiseSet.contains(promiseId);
    }


    @PreDestroy
    private void shutdown() {
        this.timer.cancel();
    }


    /**
     * 发送延迟时间
     *
     * @return
     */
    public abstract long getSendSleepTime();


    /**
     * 服务器主机地址
     *
     * @return
     */
    public abstract String getHost();


}
