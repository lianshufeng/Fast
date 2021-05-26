package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.FamilyDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.client.model.FamilyModel;
import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.helper.ReIndexHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FamilyDaoImpl implements FamilyDaoExtend {

    @Autowired
    private ReIndexHelper reIndexHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    /**
     * 每次启动都重建索引
     */
    @PostConstruct
    private void reIndex() {
        this.reIndexHelper.updateIndex(Family.class,
                new Index().named("member.uid").on("member.uid", Sort.Direction.ASC),
                new Index().named("memberUniqueIndex").on("memberUniqueIndex", Sort.Direction.ASC).unique()
        );
    }


    @Override
    public List<Family> findByMember(String epId, String[] uid) {
        return this.mongoTemplate.find(Query.query(Criteria.where("enterprise").is(Enterprise.build(epId)).and("member.uid").in(uid)), Family.class);
    }

    @Override
    public Page<Family> list(String epId, Pageable pageable) {
        Query query = new Query();
        if (StringUtils.hasText(epId)) {
            query.addCriteria(Criteria.where("enterprise").is(Enterprise.build(epId)));
        }
        return this.dbHelper.pages(query, pageable, Family.class);
    }

    @Override
    public String modifyFamily(FamilyModel model) {
        Family family = this.mongoTemplate.findById(model.getId(), Family.class);
        if (family == null) {
            return this.insertFamily(model);
        }
        modelToFamily(model, family);
        this.mongoTemplate.save(family);
        return family.getId();
    }

    @Override
    public String insertFamily(FamilyModel model) {
        Family family = new Family();
        modelToFamily(model, family);
        this.mongoTemplate.insert(family);
        return family.getId();
    }


    @Override
    public void reIndexInfo() {
        log.info("重建索引 : " + Family.class.getSimpleName());
        reIndexHelper.reIndexFromMap(Family.class, "info");
    }

    /**
     * 设置唯一索引
     *
     * @param family
     */
    private void modelToFamily(FamilyModel model, Family family) {

        //拷贝数据
        BeanUtils.copyProperties(model, family, "id", "updateTime", "createTime", "epId");

        //设置企业
        family.setEnterprise(Enterprise.build(model.getEpId()));

        //owner 唯一索引
        family.setOwnerUniqueIndex(buildUniqueIndex(model.getEpId(), model.getOwner()));

        // 成员唯一索引
        family.setMemberUniqueIndex(model.getMember().stream().map((it) -> {
            return buildUniqueIndex(model.getEpId(), it.getUid());
        }).collect(Collectors.toSet()));

    }


    /**
     * 创建唯一索引
     *
     * @param epId
     * @param uid
     * @return
     */
    private String buildUniqueIndex(String epId, String uid) {
        return HashUtil.hash(epId, uid);
    }


}
