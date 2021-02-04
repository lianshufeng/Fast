package com.fast.dev.auth.center.server.core.dataclean;

import com.fast.dev.auth.center.server.core.dao.FamilyDao;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.center.server.core.service.impl.FamilyServiceImpl;
import com.fast.dev.auth.client.model.FamilyMember;
import com.fast.dev.auth.client.type.FamilyIdentity;
import com.fast.dev.data.mongo.data.MongoDataCleanTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class CleanOnlyUserFamilyTask extends MongoDataCleanTask<Family> {

    @Autowired
    private FamilyServiceImpl familyService;

    @Autowired
    private FamilyDao familyDao;


    @Override
    public void clean(Family[] families) {
        //用户id
        Arrays.stream(families).forEach((it) -> {

            //被监护人
            AtomicReference<FamilyMember> wardAtomicReference = new AtomicReference();
            //数据清洗:不允许出现同一个家庭组的一个手机号码有多个身份
            it.getMember().stream().filter((member) -> {
                return member.getIdentity() == FamilyIdentity.Ward;
            }).findFirst().ifPresent((member) -> {
                wardAtomicReference.set(member);
            });

            //如果被监护人与监护人是同一个人，则删除监护人
            Optional.ofNullable(wardAtomicReference.get()).ifPresent((wardMember) -> {
                //需要删除的用户
                List<FamilyMember> removeMember = it.getMember().stream().filter((member) -> {
                    return member.getIdentity() == FamilyIdentity.Guardian && member.getUid().equals(wardMember.getUid());
                }).collect(Collectors.toList());
                if (removeMember.size() > 0) {
                    it.getMember().removeAll(removeMember);
                }
            });

        });
    }
}
