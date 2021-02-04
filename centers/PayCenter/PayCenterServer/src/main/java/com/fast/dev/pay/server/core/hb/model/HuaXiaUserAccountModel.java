package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.AcctType;
import com.fast.dev.pay.server.core.hb.type.IdType;
import com.fast.dev.pay.server.core.hb.type.JobsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaUserAccountModel {

    //绑定账户
    @Indexed
    private String bindCardNo;

    //身份证号码
    @Indexed
    private String idNo;

    //身份证号码
    @Indexed
    private IdType idType = IdType.IdCard;

    //真实姓名
    @Indexed
    private String userName;

    //手机号码
    @Indexed
    private String userMobile;

    /**
     * 开立账户类型
     */
    private AcctType accountType;

    //正面照片
    private String certimageface;

    //背面身份照片
    private String certimageback;

    //身份开始时间
    @Indexed
    private String idCardStartDate;

    //身份证结束时间
    @Indexed
    private String idCardEndDate;

    //常用居住地址,9个以上的汉字
    private String address;

    //职业编码
    private JobsType jobs;


}
