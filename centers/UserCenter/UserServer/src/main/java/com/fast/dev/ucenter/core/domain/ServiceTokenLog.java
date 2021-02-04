package com.fast.dev.ucenter.core.domain;

import com.fast.dev.ucenter.core.model.ClientInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTokenLog extends ServiceToken {


    /**
     * 客户端信息
     */
    private ClientInfo clientInfo;


}
