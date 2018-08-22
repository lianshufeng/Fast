package com.fast.dev.core.util.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 调用结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokerResult<T> implements Serializable {

    //状态
    private InvokerState state;

    //内容
    private T content;


    /**
     * 实例化并设置当前状态为Success
     *
     * @return
     */
    public static InvokerResult success(Object content) {
        return new InvokerResult(InvokerState.Success, content);
    }

    /**
     * 错误
     *
     * @param content
     * @return
     */
    public static InvokerResult error(Object content) {
        return new InvokerResult(InvokerState.Error, content);
    }

    /**
     * 异常
     *
     * @param content
     * @return
     */
    public static InvokerResult exception(Object content) {
        return new InvokerResult(InvokerState.Exception, content);
    }


}
