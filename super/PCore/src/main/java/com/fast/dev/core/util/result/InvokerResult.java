package com.fast.dev.core.util.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 调用结果
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvokerResult<T> implements Serializable {


    //状态
    private InvokerState state;

    //内容
    private T content;


    /**
     * 是否成功
     *
     * @param content
     * @return
     */
    public static InvokerResult isTrue(Boolean content) {
        return new InvokerResult(content ? InvokerState.Success : InvokerState.Error, content);
    }


    /**
     * 设置对象不能为空，如果为空状态则为error
     *
     * @param content
     * @return
     */
    public static InvokerResult notNull(Object content) {
        return new InvokerResult(content == null ? InvokerState.Error : InvokerState.Success, content);
    }


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
