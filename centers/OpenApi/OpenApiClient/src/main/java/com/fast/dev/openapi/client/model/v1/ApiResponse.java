package com.fast.dev.openapi.client.model.v1;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    //内容
    @Getter
    @Setter
    private String data;

    //状态
    @Getter
    @Setter
    private ResultState state;

    //文本
    @Getter
    @Setter
    private String msg;

    //当前服务器时间
    @Getter
    @Setter
    private Long time;


    public static ApiResponse build(ResultState state, String data) {
        return ApiResponse.builder().state(state).data(data).msg(state.getRemark()).build();
    }

    public static ApiResponse build(boolean bool) {
        return build(bool ? ResultState.Success : ResultState.Fail, null);
    }

    public static ApiResponse build(ResultState state) {
        return build(state, null);
    }

    public static ApiResponse buildContent(String data) {
        return build(data == null ? ResultState.Fail : ResultState.Success, data);
    }

}
