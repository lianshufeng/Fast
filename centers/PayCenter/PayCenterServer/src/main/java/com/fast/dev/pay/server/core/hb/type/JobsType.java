package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

public enum JobsType {


    StateCivilServants("国家公务员", 1),
    ProfessionalAndTechnicalPersonnel("专业技术员", 3),
    StaffMember("职员", 10),
    StaffOfStateOwnedEnterprises("国有企业职员", 11),
    StaffOfJointStockEnterprises("股份制企业职员", 12),
    StaffOfForeignFundedEnterprises("三资企业职员", 13),
    StaffOfPrivateEnterprises("名营企业职员", 14),
    PrivateSectorStaff("私营企业职员", 15),
    EnterpriseManagementPersonnel("企业管理人员", 21),
    Worker("工人", 24),
    Farmer("农名", 27),
    Student("学生", 31),
    Servicemen("现役军人", 37),
    Professional("自由职业者", 51),
    SelfEmployed("个体经营者", 54),
    UnemployedPersons("无业人员", 70),
    RetiredPersonne("退休人员", 80),
    Other("其他", 90),
    ;

    @Getter
    private String remark;
    @Getter
    private int code;

    JobsType(String remark, int code) {
        this.remark = remark;
        this.code = code;
    }
}
