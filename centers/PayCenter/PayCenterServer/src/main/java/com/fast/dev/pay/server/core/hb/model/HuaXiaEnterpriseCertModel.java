package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseCertModel extends CertModel {

    //证书文件id
    private String certFileId;

}
