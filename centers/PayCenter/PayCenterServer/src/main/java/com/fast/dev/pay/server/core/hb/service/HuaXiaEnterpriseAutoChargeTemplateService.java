package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.dao.IncTableDao;
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeTemplateDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeTemplateModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
public class HuaXiaEnterpriseAutoChargeTemplateService {

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateDao huaXiaEnterpriseAutoChargeTemplateDao;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private IncTableDao incTableDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaxiaConf huaxiaConf;

    /**
     * 获取企业套餐列表
     *
     * @param pageable
     * @return
     */
    public Page<HuaXiaEnterpriseAutoChargeTemplateModel> list(String epId, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.huaXiaEnterpriseAutoChargeTemplateDao.findByHuaXiaEnterpriseAccount(this.huaXiaEnterpriseAccountDao.findByEpId(epId), pageable), (huaXiaEnterpriseAutoChargeTemplate) -> {
            return toModel(huaXiaEnterpriseAutoChargeTemplate);
        });
    }


    /**
     * 读取订单
     *
     * @param hbCode
     * @param templateCode
     * @return
     */
    public ResultContent<HuaXiaEnterpriseAutoChargeTemplateModel> readOrder(String hbCode, String templateCode) {
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = this.huaXiaEnterpriseAccountDao.findByCode(hbCode);
        if (huaXiaEnterpriseAccount == null) {
            return ResultContent.build(ResultState.EnterpriseNotExist);
        }
        HuaXiaEnterpriseAutoChargeTemplate template = this.huaXiaEnterpriseAutoChargeTemplateDao.findByHuaXiaEnterpriseAccountAndCode(huaXiaEnterpriseAccount, templateCode);
        ResultState state = validateTemplate(template);
        if (state != null) {
            return ResultContent.build(state);
        }
        return ResultContent.buildContent(toModel(template));
    }

    /**
     * 删除一个模板
     *
     * @param id
     * @return
     */
    @Transactional
    public ResultState remove(String epId, String id) {
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = this.huaXiaEnterpriseAccountDao.findByEpId(epId);
        if (huaXiaEnterpriseAccount == null) {
            return ResultState.Fail;
        }
        return this.huaXiaEnterpriseAutoChargeTemplateDao.removeByHuaXiaEnterpriseAccountAndId(huaXiaEnterpriseAccount, id) > 0 ? ResultState.Success : ResultState.Fail;
    }


    /**
     * 信增或者更新套餐模板
     *
     * @param huaXiaEnterpriseAutoChargeTemplateModel
     * @return
     */
    @Transactional
    public ResultContent<HuaXiaEnterpriseAutoChargeTemplateModel> update(HuaXiaEnterpriseAutoChargeTemplateModel huaXiaEnterpriseAutoChargeTemplateModel) {
        //如果存在id则新建
        HuaXiaEnterpriseAutoChargeTemplate template = null;
        if (StringUtils.hasText(huaXiaEnterpriseAutoChargeTemplateModel.getId())) {
            template = this.huaXiaEnterpriseAutoChargeTemplateDao.findTop1ById(huaXiaEnterpriseAutoChargeTemplateModel.getId());
            if (!template.getHuaXiaEnterpriseAccount().getEpId().equals(huaXiaEnterpriseAutoChargeTemplateModel.getEpId())) {
                return ResultContent.build(ResultState.Fail, "非法操作");
            }
        }

        if (template == null) {
            Assert.notNull(huaXiaEnterpriseAutoChargeTemplateModel.getStartTime(), "套餐的开始时间不能为空");
            Assert.notNull(huaXiaEnterpriseAutoChargeTemplateModel.getEndTime(), "套餐的结束时间不能为空");
            Assert.hasText(huaXiaEnterpriseAutoChargeTemplateModel.getName(), "套餐名不能为空");
            Assert.notNull(huaXiaEnterpriseAutoChargeTemplateModel.getAutoChargeInfos(), "分期扣款信息不能为空");

            template = new HuaXiaEnterpriseAutoChargeTemplate();
            template.setHuaXiaEnterpriseAccount(huaXiaEnterpriseAccountDao.findByEpId(huaXiaEnterpriseAutoChargeTemplateModel.getEpId()));
            Assert.notNull(template.getHuaXiaEnterpriseAccount(), "企业id不正确");

            //设置模板编码
            template.setCode(templateCode(huaXiaEnterpriseAutoChargeTemplateModel.getEpId()));

        }

        Set<String> ignoreProperties = new HashSet<>() {{
            add("id");
            add("epId");
            add("hbId");
            add("hbCode");
            add("code");
        }};
        BeanUtil.getNullPropertyNames(huaXiaEnterpriseAutoChargeTemplateModel, ignoreProperties);
        BeanUtils.copyProperties(huaXiaEnterpriseAutoChargeTemplateModel, template, ignoreProperties.toArray(new String[0]));
        this.dbHelper.updateTime(template);
        this.huaXiaEnterpriseAutoChargeTemplateDao.save(template);

        return ResultContent.buildContent(toModel(template));
    }


    /**
     * 验证模板
     *
     * @param hbCode
     * @param code
     * @return
     */
    public ResultState validateTemplate(String hbCode, String code) {
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = this.huaXiaEnterpriseAccountDao.findByCode(hbCode);
        if (huaXiaEnterpriseAccount == null) {
            return ResultState.EnterpriseNotExist;
        }
        return validateTemplate(this.huaXiaEnterpriseAutoChargeTemplateDao.findByHuaXiaEnterpriseAccountAndCode(huaXiaEnterpriseAccount, code));
    }


    /**
     * 校验模板是否可以使用
     */
    private ResultState validateTemplate(HuaXiaEnterpriseAutoChargeTemplate template) {
        if (template == null) {
            return ResultState.TemplateNotExist;
        }

        //模板禁用
        if (template.isDisable()) {
            return (ResultState.TemplateIsDisable);
        }

        //有效时间
        long nowTime = this.dbHelper.getTime();
        if (nowTime < template.getStartTime() || nowTime > template.getEndTime()) {
            return (ResultState.TemplateNotValidTime);
        }

        return null;
    }


    /**
     * 模板编码生产策略
     *
     * @param epId
     * @return
     */
    private String templateCode(String epId) {
        //生产模板id
        long stepIndex = this.incTableDao.inc(epId + "_template");
        String ret = String.valueOf(stepIndex);
        while (ret.length() < 3) {
            ret = "0" + ret;
        }
        return ret;
    }


    /**
     * 实体转换为模型
     *
     * @param huaXiaEnterpriseAutoChargeTemplate
     * @return
     */
    HuaXiaEnterpriseAutoChargeTemplateModel toModel(HuaXiaEnterpriseAutoChargeTemplate huaXiaEnterpriseAutoChargeTemplate) {
        HuaXiaEnterpriseAutoChargeTemplateModel model = new HuaXiaEnterpriseAutoChargeTemplateModel();
        BeanUtils.copyProperties(huaXiaEnterpriseAutoChargeTemplate, model);
        //设置企业id
        HuaXiaEnterpriseAccount account = huaXiaEnterpriseAutoChargeTemplate.getHuaXiaEnterpriseAccount();
        if (account != null) {
            model.setEpId(account.getEpId());
            model.setHbId(account.getId());
            model.setHbCode(account.getCode());
            //企业工作时间
            model.setEpWorkTime(account.getWorkTime());
            //企业名称
            model.setEpName(account.getEnterpriseName());
            //企业的客服电话
            model.setCustomerPhone(account.getCustomerPhone());
        }
        return model;
    }

}
