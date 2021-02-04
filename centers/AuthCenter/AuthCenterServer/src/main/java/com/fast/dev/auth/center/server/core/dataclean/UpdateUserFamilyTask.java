package com.fast.dev.auth.center.server.core.dataclean;

import com.fast.dev.auth.center.server.core.dao.FamilyDao;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.center.server.core.service.impl.FamilyServiceImpl;
import com.fast.dev.data.mongo.data.MongoDataCleanTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
public class UpdateUserFamilyTask extends MongoDataCleanTask<Family> {

    @Autowired
    private FamilyServiceImpl familyService;

    @Autowired
    private FamilyDao familyDao;


    @Override
    public void clean(Family[] families) {
        //用户id
        Arrays.stream(families).forEach((it) -> {
            //缓存同步到用户表中
            familyService.updateUserFamily(Set.of(), it.getId());
        });
    }
}
