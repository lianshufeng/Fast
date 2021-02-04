package com.fast.dev.core.util.response.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MimeType {

    /**
     * 扩展名
     */
    private String extension;


    /**
     * 名称
     */
    private String name;

}
