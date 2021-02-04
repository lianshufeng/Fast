package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import com.fast.dev.pay.server.core.hb.model.TaskProcessModel;
import com.fast.dev.pay.server.core.hb.type.TaskState;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 华夏企业的任务表
 */
@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseTask extends SuperEntity {

    //华夏企业
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAccount huaXiaEnterpriseAccount;

    //任务类型
    @Indexed
    private TaskType type;

    //任务状态
    @Indexed
    private TaskState state;

    //合同
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAutoChargeContract contract;

    //任务进度
    @Indexed
    private TaskProcessModel taskProcess;

    //生效时间
    @Indexed
    private long effectTime;

    //参数
    @Indexed
    private Map<String, Object> parameter;


    //任务执行次数
    @Indexed
    private long executeCount;


    //任务最后一次执行时间
    @Indexed
    private long lastExecuteTime;

    //构建的任务对象
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseTask parent;


    /**
     * 写入参数
     *
     * @param parameter
     */
    @SneakyThrows
    public void writeParameter(HuaXiaEnterpriseTaskParameter parameter) {
        this.setParameter(toParameter(parameter));
    }

    /**
     * 读取参数
     *
     * @param taskType
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T> T readParameter(TaskType taskType) {
        return (T) JsonUtil.toObject(JsonUtil.toJson(this.getParameter()), taskType.getParameterEntity());
    }

    /**
     * 转换到参数
     *
     * @param parameter
     * @return
     */
    @SneakyThrows
    public static Map<String, Object> toParameter(HuaXiaEnterpriseTaskParameter parameter) {
        return JsonUtil.toObject(JsonUtil.toJson(parameter), Map.class);
    }


}
