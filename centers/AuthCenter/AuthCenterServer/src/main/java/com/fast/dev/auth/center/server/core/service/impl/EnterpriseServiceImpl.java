package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.annotations.AuthEvent;
import com.fast.dev.auth.center.server.core.annotations.CleanUserCache;
import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.EnterpriseModelAndSK;
import com.fast.dev.auth.client.model.EnterpriseQueryModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.core.util.token.TokenUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.model.QueryModel;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    @Override
    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#model", "#ownerUid"})
    @AuthEvent(filter = "#ret.state.toString() == 'Success'", parm = {"#ret.content", "#model", "#ownerUid"}, type = AuthEventType.Enterprise, action = AuthEventAction.Create)
    public ResultContent<String> add(EnterpriseModel model, String ownerUid) {
        //判断用户是否存在
        if (!StringUtils.hasText(ownerUid)) {
            return ResultContent.build(ResultState.UserNotExist);
        }
        BaseUserModel baseUserModel = remoteUserCenterService.queryUserId(ownerUid);
        if (baseUserModel == null || baseUserModel.getId() == null) {
            return ResultContent.build(ResultState.UserNotExist);
        }

        if (this.enterpriseDao.existsByName(model.getName())) {
            return ResultContent.build(ResultState.EnterpriseExist);
        }
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(model, enterprise, "id", "ak");

        //初始化 Ak与Sk
        enterprise.setAk(TokenUtil.create());
        enterprise.setSk(TokenUtil.create());

        this.dbHelper.saveTime(enterprise);

        //增加企业
        this.enterpriseDao.save(enterprise);

        return ResultContent.buildContent(enterprise.getId());
    }

    @Override
    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#id"})
    public ResultContent<String> resetSK(String id) {
        Enterprise enterprise = this.enterpriseDao.findTop1ById(id);
        if (enterprise == null) {
            return ResultContent.build(ResultState.EnterpriseNotExist);
        }
        //如果应用key为空，则重新初始化一个
        if (!StringUtils.hasText(enterprise.getAk())) {
            enterprise.setAk(TokenUtil.create());
        }
        enterprise.setSk(TokenUtil.create());
        this.enterpriseDao.save(enterprise);
        return ResultContent.build(ResultState.Success, enterprise.getSk());
    }

    @Override
    public EnterpriseModelAndSK getFromAK(String ak) {
        Enterprise enterprise = this.enterpriseDao.findTop1ByAk(ak);
        if (enterprise == null) {
            return null;
        }
        EnterpriseModelAndSK enterpriseModelAndSK = new EnterpriseModelAndSK();
        BeanUtils.copyProperties(toModel(enterprise), enterpriseModelAndSK);
        enterpriseModelAndSK.setSk(enterprise.getSk());
        return enterpriseModelAndSK;
    }


    @Override
    public Page<EnterpriseModel> list(EnterpriseModel enterpriseModel, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.enterpriseDao.list(enterpriseModel, pageable), (enterprise) -> {
            return toModel(enterprise);
        });
    }

    @Override
    @CleanUserCache(value = "#model.id", type = CleanUserCacheType.Enterprise)
    @AuthEvent(filter = "#ret !=null && #ret.toString() == 'Success'", parm = "#model", type = AuthEventType.Enterprise, action = AuthEventAction.Update)
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#model"})
    public ResultState update(EnterpriseModel model) {
        return this.enterpriseDao.update(model) ? ResultState.Success : ResultState.Fail;
    }

    @Override
    public EnterpriseModel get(String id) {
        return toModel(this.enterpriseDao.findTop1ById(id));
    }

    /**
     * 自定义查询企业信息
     *
     * @param enterpriseQueryModel
     * @param pageable
     * @return
     */
    @Override
    public Page<EnterpriseModel> queryEnterprise(EnterpriseQueryModel enterpriseQueryModel, Pageable pageable) {
        QueryModel queryModel = new QueryModel();
        BeanUtils.copyProperties(enterpriseQueryModel, queryModel);
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.queryByMql(queryModel, pageable, Enterprise.class), (enterprise) -> {
            return toModel(enterprise);
        });
    }


    /**
     * 实体转换为模型
     *
     * @param enterprise
     * @return
     */
    private EnterpriseModel toModel(Enterprise enterprise) {
        if (enterprise == null) {
            return null;
        }
        EnterpriseModel model = new EnterpriseModel();
        BeanUtils.copyProperties(enterprise, model);
        return model;
    }


}
