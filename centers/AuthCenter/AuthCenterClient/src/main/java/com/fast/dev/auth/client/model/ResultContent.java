package com.fast.dev.auth.client.model;

import com.fast.dev.auth.client.type.ResultState;
import lombok.*;

import java.util.Optional;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ResultContent<T> {

    /**
     * 结果状态
     */
    private ResultState state;


    /**
     * 内容
     */
    private T content;


    /**
     * 状态描述
     */
    private String msg;


    /**
     * 推荐，避免null
     *
     * @return
     */
    public Optional<T> optionalContent() {
        return Optional.ofNullable(this.content);
    }

    /**
     * 构建
     *
     * @param state
     * @param content
     * @param <T>
     * @return
     */
    public static <T> ResultContent build(ResultState state, T content) {
        return ResultContent.builder().state(state).content(content).msg(state.getRemark()).build();
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
