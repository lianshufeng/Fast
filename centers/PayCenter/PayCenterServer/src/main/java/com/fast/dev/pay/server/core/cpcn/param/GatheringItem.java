package com.fast.dev.pay.server.core.cpcn.param;

import lombok.Data;

@Data
public class GatheringItem extends Item {
    private String contractNo;
    private String contractUserID;
    private String splitType;
    private String settlementFlag;
    private String splitResult;
    private String reserve1;
    private String reserve2;
    private String reserve3;
}
