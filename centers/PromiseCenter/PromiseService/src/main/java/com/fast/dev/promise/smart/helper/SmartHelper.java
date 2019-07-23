package com.fast.dev.promise.smart.helper;

import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.service.RemoteTaskService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

@Log
@Component
public class SmartHelper {

    @Autowired
    private RemoteTaskService remoteTaskService;

    /**
     * 创建实例
     *
     * @param requestParmModel
     * @return
     */
    public TaskHandle build(RequestParmModel requestParmModel) {
        TaskHandle taskHandle = null;
        RequestParmModel ret = remoteTaskService.put(requestParmModel);
        if (ret != null && !StringUtils.isEmpty(ret.getId())) {
            taskHandle = new TaskHandle();
            taskHandle.setParmModel(ret);
            taskHandle.startHeartbeatTimer();
        }
        return taskHandle;

    }


    /**
     * 句柄对象
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TaskHandle {

        private RequestParmModel parmModel;

        //定时器
        private Timer timer;


        //最大心跳次数
        private final static int MaxHeartbeatCount = 5;

        /**
         * 关闭心跳定时器
         */
        private void closeHeartbeatTimer() {
            this.timer.cancel();
            this.timer = null;
        }

        /**
         * 开始心跳定时器
         */
        protected void startHeartbeatTimer() {
            //取出定时器时钟
            this.timer = new Timer();
            //发送心跳包
            sendHeartbeat();
        }

        /**
         * 发送心跳包
         */
        private void sendHeartbeat() {
            if (this.timer == null) {
                return;
            }

            log.info(" sendHeartbeat  : " + this.parmModel.getId());

            //发送心跳包
            remoteTaskService.heartbeat(this.parmModel.getId());

            //调度
            long time = this.parmModel.getExecuteTime() / MaxHeartbeatCount;
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendHeartbeat();
                }
            }, time);
        }


        /**
         * 执行任务
         */
        public void doit() {
            closeHeartbeatTimer();
            remoteTaskService.doit(this.parmModel.getId());
        }


        /**
         * 删除任务
         */
        public void remove() {
            closeHeartbeatTimer();
            remoteTaskService.remove(this.parmModel.getId());
        }


    }
}
