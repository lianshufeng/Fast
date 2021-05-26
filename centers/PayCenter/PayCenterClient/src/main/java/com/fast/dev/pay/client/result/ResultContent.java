package com.fast.dev.pay.client.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Optional;

/**
 * 结果
 *
 * @param <T>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultContent<T> {

    //内容
    @Getter
    @Setter
    private T content;

    //状态
    @Getter
    @Setter
    private ResultState state;

    //文本
    @Getter
    @Setter
    private String msg;


    //异常
    @Getter
    @Setter
    private ResultException exception;


    /**
     * 获取内容
     *
     * @return
     */
    public Optional<T> optionalContent() {
        return Optional.ofNullable(this.content);
    }

    public static <T> ResultContent build(ResultState state, T content) {
        return ResultContent.builder().state(state).content(content).msg(state.getRemark()).build();
    }

    public static <T> ResultContent build(ResultState state, T content,String msg) {
        return ResultContent.builder().state(state).content(content).msg(msg).build();
    }

    public static <T> ResultContent build(boolean bool) {
        return build(bool ? ResultState.Success : ResultState.Fail, null);
    }

    public static <T> ResultContent build(ResultState state) {
        return build(state, null);
    }

    public static <T> ResultContent buildContent(T content) {
        return build(content == null ? ResultState.Fail : ResultState.Success, content);
    }

}
