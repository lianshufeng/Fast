package com.fast.dev.core.util.result.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果集异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultException {

    private String type;
    private String cls;
    private String message;


    /**
     * 创建异常对象
     *
     * @param e
     * @return
     */
    public static ResultException build(Exception e) {
        ResultException exception = new ResultException();
        exception.type = e.getClass().getSimpleName();
        exception.cls = e.getClass().getName();
        exception.message = e.getMessage();
        return exception;
    }
}
