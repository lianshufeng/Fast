package com.fast.dev.pay.server.core.general.service.impl;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
import com.fast.dev.pay.client.model.userbank.UserBankCardModel;
import com.fast.dev.pay.client.model.userbank.UserBankPreBindCardModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.service.UserBankCardService;
import com.fast.dev.pay.client.util.DataEncryptUtil;
import com.fast.dev.pay.server.core.cpcn.param.response.*;
import com.fast.dev.pay.server.core.cpcn.util.GUIDGenerator;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.dao.PreUserBankCardDao;
import com.fast.dev.pay.server.core.general.dao.UserBankCardDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.PreUserBankCard;
import com.fast.dev.pay.server.core.general.domain.UserBankCard;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.cpcn.CpcnHelper;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserBankCardServiceImpl implements UserBankCardService {

    @Autowired
    private UserBankCardDao userBankCardDao;

    @Autowired
    private PreUserBankCardDao preUserBankCardDao;


    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;

    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    /**
     * 校验用户的银行卡是否正确
     */
    private void validate(UserBankBindCardModel userBankBindCard) {
        Assert.hasText(userBankBindCard.getPayAccountId(), "支付账户ID不能为空");
        Assert.hasText(userBankBindCard.getUid(), "用户id不能为空");

        Assert.hasText(userBankBindCard.getAccountNumber(), "银行卡号不能为空");
        Assert.hasText(userBankBindCard.getAccountNumber(), "银行卡的开户人不能为空");
        Assert.hasText(userBankBindCard.getPhoneNumber(), "银行卡的预留手机号码不能为空");
        Assert.hasText(userBankBindCard.getIdentificationNumber(), "银行卡预留的身份证号码不能为空");

    }

    /**
     * 绑定中金的银行卡
     *
     * @param userBankBindCard
     */
    private ResultContent<String> callCPCNBindCard(final CpcnHelper cpcnHelper, UserBankBindCardModel userBankBindCard) {
        //预绑定
        userBankBindCard.setTxSNBinding(GUIDGenerator.genGUID());
        PreBindCardResponse preBindCardResponse = cpcnHelper.preBindCard(userBankBindCard);
        if (preBindCardResponse.getCode().equals("2000")) {
            return null;
        }
        return ResultContent.build(ResultState.Fail, null, preBindCardResponse.getMessage());
    }

    /**
     * 绑定中金的银行卡
     *
     * @param userBankBindCard
     */
    private ResultContent<String> callCPCNDirectBindCard(final CpcnHelper cpcnHelper, UserBankBindCardModel userBankBindCard) {
        //预绑定
        userBankBindCard.setTxSNBinding(GUIDGenerator.genGUID());
        DirectBindCardResponse directBindCardResponse = cpcnHelper.directBindCard(userBankBindCard);
        if (directBindCardResponse.getCode().equals("2000")) {
            return null;
        }
        return ResultContent.build(ResultState.Fail, null, directBindCardResponse.getMessage());
    }

    /**
     * 查询是否绑定
     *
     * @return
     */
    private ResultState queryBindCard(final CpcnHelper cpcnHelper, UserBankBindCardModel userBankBindCard) {
        //查询是否绑定
        QueryBindCardResponse queryBindCardResponse = cpcnHelper.queryBindCard(userBankBindCard);
        userBankBindCard.setTxSNBinding(queryBindCardResponse.getTxSNBinding());
        if (queryBindCardResponse.getStatus() == 10) {
            return ResultState.UserBankCardExist;
        }
        return ResultState.Success;
    }

    @Override
    public Page<UserBankCardModel> listFromEnterprise(String epId, String uid, Pageable pageable) {
        //查询这些企业下的所有支付账号
        Set<String> payAccountIds = this.enterprisePayAccountDao.findByEnterpriseId(epId).stream().map((it) -> {
            return it.getId();
        }).collect(Collectors.toSet());
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.pages(
                Query.query(
                        Criteria.where("uid").is(uid).and("payAccountId").in(payAccountIds)
                ),
                pageable,
                UserBankCard.class
        ), (it) -> {
            UserBankCardModel model = new UserBankCardModel();
            BeanUtils.copyProperties(it, model);
            return model;
        });
    }

    @Override
    public Page<UserBankCardModel> list(String payAccountId, String uid, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.pages(
                Query.query(
                        Criteria.where("uid").is(uid).and("payAccountId").is(payAccountId)
                ),
                pageable,
                UserBankCard.class
        ), (it) -> {
            UserBankCardModel model = new UserBankCardModel();
            BeanUtils.copyProperties(it, model);
            return model;
        });
    }

    @Override
    @UserLog(parameter = "#userBankBindCard")
    public ResultContent<String> preBind(UserBankPreBindCardModel preBindCardModel) {

        UserBankBindCardModel userBankBindCard = new UserBankBindCardModel();
        BeanUtils.copyProperties(preBindCardModel,userBankBindCard,"phoneNumber");

        //查询用户的手机号
        BaseUserModel userModel = remoteUserCenterService.queryUserId(preBindCardModel.getUid());
        Assert.notNull(userModel,"用户不存在");
        Assert.hasText(userModel.getPhone(),"用户手机号不存在");
        userBankBindCard.setPhoneNumber(userModel.getPhone());

        //校验银行卡
        validate(userBankBindCard);


        //查询企业支付账户
        EnterprisePayAccount enterprisePayAccount = enterprisePayAccountDao.findTop1ById(userBankBindCard.getPayAccountId());

        //加密后的数据模型
        UserBankBindCardModel encryptModel = encryptModel(enterprisePayAccount, userBankBindCard);

        final CpcnHelper cpcnHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnHelper.class);

        //存在银行卡
        if (this.userBankCardDao.existsCard(encryptModel.getUid(), enterprisePayAccount.getId(), encryptModel.getAccountNumber())) {
            return ResultContent.build(ResultState.UserBankCardExist);
        }

        //已存在预绑定
        if (this.preUserBankCardDao.existsCard(encryptModel.getUid(), enterprisePayAccount.getId(), encryptModel.getAccountNumber())) {
            preUserBankCardDao.delCard(encryptModel.getUid(), enterprisePayAccount.getId(), encryptModel.getAccountNumber());
            //return ResultContent.build(ResultState.PreBindCardExist);
        }

        //用户已经绑定
        if (queryBindCard(cpcnHelper, userBankBindCard) == ResultState.UserBankCardExist) {
            encryptModel.setTxSNBinding(userBankBindCard.getTxSNBinding());
            saveUserBankCard(encryptModel);
            return ResultContent.build(ResultState.UserBankCardExist);
        }

        //调用中金银行的预绑定接口
        ResultContent<String> state = callCPCNBindCard(cpcnHelper, userBankBindCard);
        if (state != null) {
            return state;
        }

        //进入本地库
        encryptModel.setTxSNBinding(userBankBindCard.getTxSNBinding());
        return ResultContent.buildContent(this.preUserBankCardDao.insert(encryptModel));
    }

    @Override
    @UserLog(parameter = {"#preBindId", "#code"})
    public ResultContent<String> bind(String preBindId, String code) {

        Assert.isTrue(code.length() == 6, "验证码必须是六位");
        PreUserBankCard preUserBankCard = this.preUserBankCardDao.findAndRemoveById(preBindId);
        if (preUserBankCard == null) {
            return ResultContent.build(ResultState.PreBindCardNoExist);
        }
        //查询企业支付账户
        EnterprisePayAccount enterprisePayAccount = enterprisePayAccountDao.findTop1ById(preUserBankCard.getPayAccountId());
        final CpcnHelper cpcnHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnHelper.class);
        ConfirmBindCardResponse response = cpcnHelper.confirmBindCard(preUserBankCard.getTxSNBinding(), code);
        if (response.getStatus() == 30) {
            UserBankBindCardModel encryptModel = new UserBankBindCardModel();
            BeanUtils.copyProperties(preUserBankCard, encryptModel);
            return ResultContent.buildContent(saveUserBankCard(encryptModel));
        }
        return ResultContent.build(ResultState.Fail, null, response.getResponseMessage());
    }

    @Override
    public ResultContent<String> directBind(UserBankBindCardModel userBankBindCard) {
        //校验银行卡
        validate(userBankBindCard);


        //查询企业支付账户
        EnterprisePayAccount enterprisePayAccount = enterprisePayAccountDao.findTop1ById(userBankBindCard.getPayAccountId());

        //加密后的数据模型
        UserBankBindCardModel encryptModel = encryptModel(enterprisePayAccount, userBankBindCard);

        final CpcnHelper cpcnHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnHelper.class);

        //存在银行卡
        if (this.userBankCardDao.existsCard(encryptModel.getUid(), enterprisePayAccount.getEnterpriseId(), encryptModel.getAccountNumber())) {
            return ResultContent.build(ResultState.UserBankCardExist);
        }

        //已存在预绑定
        if (this.preUserBankCardDao.existsCard(encryptModel.getUid(), enterprisePayAccount.getEnterpriseId(), encryptModel.getAccountNumber())) {
            return ResultContent.build(ResultState.PreBindCardExist);
        }

        //用户已经绑定
        if (queryBindCard(cpcnHelper, userBankBindCard) == ResultState.UserBankCardExist) {
            encryptModel.setTxSNBinding(userBankBindCard.getTxSNBinding());
            saveUserBankCard(encryptModel);
            return ResultContent.build(ResultState.UserBankCardExist);
        }

        //调用中金银行的绑定接口
        ResultContent<String> state = callCPCNDirectBindCard(cpcnHelper, userBankBindCard);
        if (state != null) {
            return state;
        }

        //进入本地库
        encryptModel.setTxSNBinding(userBankBindCard.getTxSNBinding());
        return ResultContent.buildContent(saveUserBankCard(encryptModel));
    }

    private String saveUserBankCard(UserBankBindCardModel encryptModel) {
        //进入本地库
        return this.userBankCardDao.insert(encryptModel);
    }


    @Override
    @UserLog(parameter = {"#id"})
    public ResultState unBind(String id) {
        UserBankCard userBankCard = this.userBankCardDao.findTop1ById(id);
        if (userBankCard == null) {
            return ResultState.UserBankCardNotExist;
        }

        //todo 调用中金银行的取消绑定功能
        //查询企业支付账户
        EnterprisePayAccount enterprisePayAccount = enterprisePayAccountDao.findTop1ById(userBankCard.getPayAccountId());
        final CpcnHelper cpcnHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnHelper.class);
        CancelBindCardResponse response = cpcnHelper.cancelBindCard(userBankCard.getTxSNBinding());
        if (response.getStatus() == 20) {
            this.userBankCardDao.deleteById(id);
            return ResultState.Success;
        }
        return ResultState.Fail;
    }


    /**
     * 加密数据模型
     *
     * @param userBankBindCard
     * @return
     */
    private UserBankBindCardModel encryptModel(EnterprisePayAccount enterprisePayAccount, UserBankBindCardModel userBankBindCard) {
        EnterpriseModel enterprise = enterpriseService.get(enterprisePayAccount.getEnterpriseId());
        Assert.notNull(enterprise, "企业id不正确");

        String ak = enterprise.getAk();
        if (!StringUtils.hasText(ak)) {
            this.enterpriseService.resetSK(enterprise.getId());
            enterprise = enterpriseService.get(enterprise.getId());
        }


        UserBankBindCardModel model = new UserBankBindCardModel();

        model.setUid(userBankBindCard.getUid());
        model.setPayAccountId(userBankBindCard.getPayAccountId());
        model.setPhoneNumber(DataEncryptUtil.encrypt(userBankBindCard.getPhoneNumber(), ak));
        model.setAccountName(DataEncryptUtil.encrypt(userBankBindCard.getAccountName(), ak));
        model.setAccountNumber(DataEncryptUtil.encrypt(userBankBindCard.getAccountNumber(), ak));
        model.setIdentificationNumber(DataEncryptUtil.encrypt(userBankBindCard.getIdentificationNumber(), ak));


        return model;
    }


}
