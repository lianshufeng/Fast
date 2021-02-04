package com.fast.dev.pay.server.core.general.service.impl;

import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.annotations.ValidateCert;
import com.fast.dev.pay.client.model.EnterprisePayAccountModel;
import com.fast.dev.pay.client.model.SimpleEnterprisePayAccountModel;
import com.fast.dev.pay.client.model.account.SuperAccount;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.service.EnterprisePayAccountService;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.util.CertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnterpriseAccountServiceImpl implements EnterprisePayAccountService {


    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private EnterpriseService enterpriseService;

    @Override
    @Transactional
    public ResultContent<SimpleEnterprisePayAccountModel> update(EnterprisePayAccountModel payAccountModel) {
        Assert.hasText(payAccountModel.getEnterpriseId(), "企业id不能为空");
        Assert.notNull(payAccountModel.getAccountType(), "账户类型不能为空");
        Assert.notNull(enterpriseService.get(payAccountModel.getEnterpriseId()), "企业id不正确");

        //校验企业支付账号
        validateEnterprisePayAccount(payAccountModel);
        String id = enterprisePayAccountDao.update(payAccountModel);
        return ResultContent.buildContent(toSimpleEnterprisePayAccountModel(this.enterprisePayAccountDao.findTop1ById(id)));
    }

    @Override
    @Transactional
    public ResultContent remove(String... id) {
        return ResultContent.build(this.enterprisePayAccountDao.deleteByIdIn(id) > 0);
    }


    /**
     * 校验企业id并删除企业的支付账号
     *
     * @param epId
     * @param id
     * @return
     */
    @Transactional
    public ResultContent remove(String epId, String[] id) {
        return ResultContent.build(this.enterprisePayAccountDao.deleteByEnterpriseIdAndIdIn(epId, id) > 0);
    }


    @Override
    public ResultContent<List<EnterprisePayAccountModel>> list(String enterpriseId) {
        Assert.hasText(enterpriseId, "企业id不能为空");
        return ResultContent.buildContent(enterprisePayAccountDao.findByEnterpriseId(enterpriseId).stream().map((it) -> {
            return toModel(it);
        }).collect(Collectors.toList()));
    }

    @Override
    public ResultContent<EnterprisePayAccountModel> get(String id) {
        Assert.hasText(id, "企业支付id不能为空");
        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(id);
        Assert.notNull(enterprisePayAccount, "企业支付账户不存在");
        return ResultContent.buildContent(toModel(enterprisePayAccount));
    }


    /**
     * 转换到模型1
     *
     * @param enterprisePayAccount
     * @return
     */
    public EnterprisePayAccountModel toModel(EnterprisePayAccount enterprisePayAccount) {
        EnterprisePayAccountModel enterprisePayAccountModel = new EnterprisePayAccountModel();
        BeanUtils.copyProperties(enterprisePayAccount, enterprisePayAccountModel);
        return enterprisePayAccountModel;
    }


    /**
     * 校验支付账号必填项
     *
     * @param payAccountModel
     */
    private void validateEnterprisePayAccount(EnterprisePayAccountModel payAccountModel) {
        AccountType accountType = payAccountModel.getAccountType();
        //如果账户信息不为空则全部需要修改
        if (payAccountModel.getAccount().size() == 0) {
            return;
        }

        long lastCertValidTime = Long.MAX_VALUE;

        final Map<String, Object> account = payAccountModel.getAccount();
        //具体的账号数据类型
        Class<? extends SuperAccount> superAccount = accountType.getType();

        //取出所有属性
        for (Field field : superAccount.getDeclaredFields()) {
            //需要校验的证书
            ValidateCert validateCert = field.getAnnotation(ValidateCert.class);
            if (validateCert == null) {
                continue;
            }

            //证书校验
            String certBase64 = (String) account.get(field.getName());

            //取出证书到期时间
            long validTime = CertUtil.readCertValidTime(Base64Utils.decodeFromString(certBase64));
            if (validTime < lastCertValidTime) {
                lastCertValidTime = validTime;
            }
        }


        Map<String, Class> beanTypes = BeanUtil.readBeanType(accountType.getType());
        for (String name : beanTypes.keySet()) {
            String certBase64 = (String) account.get(name);
            Assert.notNull(certBase64, payAccountModel.getAccountType() + " 账户 , [" + name + "] 不能为空");
        }
        payAccountModel.setLastCertValidTime(lastCertValidTime);
    }


    /**
     * 转换到企业支付账户
     *
     * @param enterprisePayAccount
     * @return
     */
    private SimpleEnterprisePayAccountModel toSimpleEnterprisePayAccountModel(EnterprisePayAccount enterprisePayAccount) {
        SimpleEnterprisePayAccountModel simpleEnterprisePayAccountModel = new SimpleEnterprisePayAccountModel();
        BeanUtils.copyProperties(enterprisePayAccount, simpleEnterprisePayAccountModel);
        return simpleEnterprisePayAccountModel;
    }

}
