package com.fast.dev.pay.server.core.hb.model;

import lombok.Data;

@Data
public class ContractRequestOpenApiModel extends ContractRequestModel{

    private Integer page;

    private Integer size;

    private String id;
}
