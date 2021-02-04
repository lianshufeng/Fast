package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.stream.StreamUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.general.domain.UserAccount;
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.controller.user.HuaXiaEnterpriseAutoChargeContractController;
import com.fast.dev.pay.server.core.hb.dao.*;
import com.fast.dev.pay.server.core.hb.domain.*;
import com.fast.dev.pay.server.core.hb.model.*;
import com.fast.dev.pay.server.core.hb.model.req.FreezeAcctReq;
import com.fast.dev.pay.server.core.hb.model.req.OpenAcctReq;
import com.fast.dev.pay.server.core.hb.model.resp.OpenAcctResp;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.type.*;
import com.fast.dev.pay.server.core.hb.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HuaXiaEnterpriseAutoChargeContractService {

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractDao huaXiaEnterpriseAutoChargeContractDao;

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateDao huaXiaEnterpriseAutoChargeTemplateDao;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaEnterpriseTaskManagerService huaXiaEnterpriseTaskManagerService;


    @Autowired
    private HuaXiaApiService huaXiaApiService;


    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;


    @Autowired
    private HuaXiaEnterpriseTaskJournalDao huaXiaEnterpriseTaskJournalDao;

    @Autowired
    private UserMobileValidateDao userMobileValidateDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HuaxiaConf huaxiaConf;

    @Autowired
    private AuthClientUserHelper userHelper;


    @Transactional
    public ResultContent<HuaXiaEnterpriseUserOpenAccountInfoModel> openAccount(SuperAutoChargeContractModel model, SuperAutoChargeModel autoCharge) {
        log.info("openAccount : {}", model);
        //todo 华夏开户
        HuaXiaApiService.ApiResult apiResult = this.huaXiaApiService.openAccount(model);
        if (apiResult.isSuccess() == false) {
            return ResultContent.build(ResultState.Fail, apiResult.getFailReason());
        }
        return this.add(model, autoCharge);
    }

    @Transactional
    public ResultContent<HuaXiaEnterpriseUserOpenAccountInfoModel> insertContract(SuperAutoChargeContractModel model, SuperAutoChargeModel autoCharge) {
        String userMobile = model.getUserAccount().getUserMobile();
        HuaXiaEnterpriseAutoChargeContract contract = huaXiaEnterpriseAutoChargeContractDao.findTop1ByUserAccount_UserMobile(userMobile);
        model.setUserAccount(contract.getUserAccount());
        model.setUserOpenAccountInfo(contract.getUserOpenAccountInfo());
        return this.add(model, autoCharge);
    }


    @Transactional
    public ResultContent<HuaXiaEnterpriseUserOpenAccountInfoModel> add(SuperAutoChargeContractModel model, SuperAutoChargeModel autoCharge) {
        //华夏企业账户
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findByEpId(model.getEpId());
        Assert.notNull(account, "企业不存在");
        Assert.notNull(model.getUserAccount(), "用户的开户信息不能为空");
        Assert.hasText(model.getConsumePhone(), "消费者手机不能为空");


        HuaXiaEnterpriseAutoChargeContract contract = new HuaXiaEnterpriseAutoChargeContract();
        BeanUtils.copyProperties(model, contract, "contractState", "contractFreeze", "lastChargeContract");

        //设置企业账户
        contract.setHuaXiaEnterpriseAccount(account);

        //设置扣款的模板信息
        contract.setAutoCharge(autoCharge);

        //设置初始化状态
        contract.setContractState(ContractState.WaitFreeze);

        //设置订单状态
        contract.setContractFreeze(new ContractFreezeModel(0, ContractFreezeState.Wait));

        //设置套餐订单
        if (StringUtils.hasText(model.getTemplateId())) {
            contract.setTemplate(this.huaXiaEnterpriseAutoChargeTemplateDao.findTop1ById(model.getTemplateId()));
        }


        this.dbHelper.saveTime(contract);
        this.huaXiaEnterpriseAutoChargeContractDao.save(contract);

        //添加冻结任务
        this.huaXiaEnterpriseTaskManagerService.addFreezeTask(contract);


        return ResultContent.buildContent(model.getUserOpenAccountInfo());
    }

    /***
     * 修改合同状态
     * @param id
     * @param state
     */
    @Transactional
    public void updateState(String id, ContractState state) {
        this.huaXiaEnterpriseAutoChargeContractDao.updateState(id, state);
    }

    /**
     * 更新冻结状态
     *
     * @param id
     * @param contractFreeze
     */
    @Transactional
    public void updateFreeze(String id, ContractFreezeModel contractFreeze) {
        this.huaXiaEnterpriseAutoChargeContractDao.updateFreeze(id, contractFreeze);
    }

    @Transactional
    public boolean cancel(String epId, String id) {
        //查询到这个合同
        HuaXiaEnterpriseAutoChargeContract contract = this.huaXiaEnterpriseAutoChargeContractDao.findByHuaXiaEnterpriseAccountAndId(this.huaXiaEnterpriseAccountDao.findByEpId(epId), id);
        if (contract == null) {
            return false;
        }

//        不允许结束已完成的任务
        if (contract.getContractState() == ContractState.Finish || contract.getContractState() == ContractState.Cancel) {
            return false;
        }

        //更新合同状态为终止
        this.huaXiaEnterpriseAutoChargeContractDao.updateState(id, ContractState.Cancel);

        //更新任务状态为终止
        for (TaskType taskType : TaskType.values()) {
            this.huaXiaEnterpriseTaskDao.findByContractAndType(contract, taskType).forEach((task) -> {
                this.huaXiaEnterpriseTaskDao.updateTaskState(task.getId(), TaskState.Cancel);
            });
        }

        return true;
    }


    /**
     * 合同详情
     *
     * @param epId
     * @param id
     * @return
     */
    public HuaXiaEnterpriseAutoChargeContractDetails details(String epId, String id) {
        HuaXiaEnterpriseAutoChargeContract contract = this.huaXiaEnterpriseAutoChargeContractDao.findByHuaXiaEnterpriseAccountAndId(this.huaXiaEnterpriseAccountDao.findByEpId(epId), id);
        if (contract == null) {
            return null;
        }
        return toDetails(contract);
    }


    /**
     * 查询用户合同
     *
     * @param contractModel
     * @return
     */
    public Page<HuaXiaEnterpriseAutoChargeContractItem> list(String epId, ContractRequestModel contractModel, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.huaXiaEnterpriseAutoChargeContractDao.list(epId, contractModel, pageable), (contract) -> {
            return toItem(contract);
        });
    }


    /**
     * 转换到详情模型
     *
     * @param contract
     * @return
     */
    public HuaXiaEnterpriseAutoChargeContractDetails toDetails(HuaXiaEnterpriseAutoChargeContract contract) {
        HuaXiaEnterpriseAutoChargeContractDetails details = new HuaXiaEnterpriseAutoChargeContractDetails();

        //扣款人姓名
        details.setUserName(contract.getUserAccount().getUserName());

        //扣款人电话
        details.setUserPhone(contract.getUserAccount().getUserMobile());

        //消费者手机
        details.setConsumePhone(contract.getConsumePhone());

        //设置订单名词
        details.setOrderName(contract.getOrderName());


        //套餐总金额
        long totalAmount = 0;
        for (AutoChargeInfo chargeInfo : contract.getAutoCharge().getAutoChargeInfos()) {
            totalAmount += chargeInfo.getAmount();
        }
        details.setTotalAmount(totalAmount);


        //冻结任务
        HuaXiaEnterpriseTask freezeTask = this.huaXiaEnterpriseTaskDao.findByContractAndType(contract, TaskType.Freeze).get(0);
        details.setFreezeTask(toFreezeTask(freezeTask));


        //扣款详情
        List<HuaXiaEnterpriseAutoChargeContractDetails.ChargeTask> chargeTasks = new ArrayList<>();
        for (AutoChargeInfo autoChargeInfo : contract.getAutoCharge().getAutoChargeInfos()) {
            chargeTasks.add(toChargeTask(autoChargeInfo));
        }


        //扣款任务
        details.setChargeTasks(chargeTasks);

        //套餐备注
        details.setRemark(contract.getAutoCharge().getRemark());

        return details;
    }


    /**
     * 转换为模型
     *
     * @return
     */
    public HuaXiaEnterpriseAutoChargeContractModel toModel(HuaXiaEnterpriseAutoChargeContract contract) {
        HuaXiaEnterpriseAutoChargeContractModel model = new HuaXiaEnterpriseAutoChargeContractModel();
        BeanUtils.copyProperties(contract, model);

        //设置企业
        model.setEpId(contract.getHuaXiaEnterpriseAccount().getEpId());


        return model;
    }


    /**
     * 转到集合项
     *
     * @param contract
     * @return
     */
    public HuaXiaEnterpriseAutoChargeContractItem toItem(HuaXiaEnterpriseAutoChargeContract contract) {
        HuaXiaEnterpriseAutoChargeContractModel model = toModel(contract);
        HuaXiaEnterpriseAutoChargeContractItem item = new HuaXiaEnterpriseAutoChargeContractItem();
        BeanUtils.copyProperties(model, item);


        //总金额
        long totalAmount = 0;
        for (AutoChargeInfo chargeInfo : contract.getAutoCharge().getAutoChargeInfos()) {
            totalAmount += chargeInfo.getAmount();
        }
        item.setTotalAmount(totalAmount);


        //查询扣款任务
        List<HuaXiaEnterpriseTask> tasks = StreamUtil.iterableToStream(
                this.huaXiaEnterpriseTaskDao.findAllById(Arrays.stream(contract
                        .getAutoCharge()
                        .getAutoChargeInfos())
                        .map((it) -> {
                            return it.getTaskId();
                        }).collect(Collectors.toList())), true).collect(Collectors.toList()
        );

        if (tasks != null && tasks.size() > 0) {
            //汇总已扣所有金额
            long chargeAmount = 0;
            for (HuaXiaEnterpriseTask task : tasks) {
                HuaXiaEnterpriseChargeTaskParameter parameter = task.readParameter(TaskType.Charge);
                chargeAmount += parameter.getPaymentAmount();
            }
            item.setChargeAmount(chargeAmount);

            //任务排序，根据先后扣款的顺序
            Collections.sort(tasks, (task1, task2) -> {
                return task1.getEffectTime() > task2.getEffectTime() ? 1 : -1;
            });
            //开始扣款时间 = 冻结成功时间+第一笔扣款时间
            item.setStartChargeTime(tasks.get(0).getEffectTime());
            //结束合同时间
            item.setEndChargeTime(tasks.get(tasks.size() - 1).getEffectTime());
        }


        //为了保护用户隐私，不显示开户信息
//        item.setUserAccount(null);
        item.setUserOpenAccountInfo(null);


        HuaXiaUserAccountModel userAccountModel = new HuaXiaUserAccountModel();
        userAccountModel.setUserMobile(item.getUserAccount().getUserMobile());
        userAccountModel.setUserName(item.getUserAccount().getUserName());
        item.setUserAccount(userAccountModel);


        //冻结状态
        if (contract.getContractFreeze().getState() != null) {
            item.setFreezeState(ContractRequestModel.FreezeState.build(contract.getContractFreeze().getState()));
        }

        //合同状态
        if (contract.getContractState() != null) {
            item.setOrderState(ContractRequestModel.OrderState.build(contract.getContractState()));
        }

        //扣款状态，没有扣款的也算成功
        if (contract.getLastChargeContract() == null) {
            item.setChargeState(ContractRequestModel.ChargeState.Success);
        } else if (contract.getLastChargeContract() != null && contract.getLastChargeContract().getState() != null) {
            item.setChargeState(ContractRequestModel.ChargeState.build(contract.getLastChargeContract().getState()));
        }


        return item;
    }


    /**
     * 转换到冻结任务模型
     *
     * @param freezeTask
     * @return
     */
    private HuaXiaEnterpriseAutoChargeContractDetails.FreezeTask toFreezeTask(HuaXiaEnterpriseTask freezeTask) {
        HuaXiaEnterpriseAutoChargeContractDetails.FreezeTask freezeTaskModel = new HuaXiaEnterpriseAutoChargeContractDetails.FreezeTask();
        freezeTaskModel.setTime(freezeTask.getCreateTime());
        freezeTaskModel.setTaskId(freezeTask.getId());
        freezeTaskModel.setState(freezeTask.getState());
        List<HuaXiaEnterpriseAutoChargeContractDetails.FreezeJournal> freezeJournals = new ArrayList<>();
        //冻结任务的流水
        this.huaXiaEnterpriseTaskJournalDao.findTop10ByHuaXiaEnterpriseTaskOrderByCreateTimeDesc(freezeTask).stream().sorted((t1, t2) -> {
            return t1.getCreateTime() > t1.getCreateTime() ? 1 : -1;
        }).forEach((journal) -> {
            HuaXiaEnterpriseAutoChargeContractDetails.FreezeJournal freezeJournal = new HuaXiaEnterpriseAutoChargeContractDetails.FreezeJournal();
            freezeJournal.setState(journal.getState());
            freezeJournal.setTime(journal.getCreateTime());
            freezeJournals.add(freezeJournal);
        });


        freezeTaskModel.setJournals(freezeJournals);
        return freezeTaskModel;
    }


    /**
     * 转换为扣款任务
     *
     * @param autoChargeInfo
     * @return
     */
    private HuaXiaEnterpriseAutoChargeContractDetails.ChargeTask toChargeTask(AutoChargeInfo autoChargeInfo) {
        HuaXiaEnterpriseAutoChargeContractDetails.ChargeTask chargeTask = new HuaXiaEnterpriseAutoChargeContractDetails.ChargeTask();
        //扣款金额
        chargeTask.setAmount(autoChargeInfo.getAmount());

        //扣款时间
        String taskId = autoChargeInfo.getTaskId();
        HuaXiaEnterpriseTask task = null;
        if (StringUtils.hasText(taskId)) {
            task = this.huaXiaEnterpriseTaskDao.findTop1ById(taskId);
        }
        if (task == null) {
            return chargeTask;
        }

        //取出扣款任务的参数
        HuaXiaEnterpriseChargeTaskParameter parameter = task.readParameter(TaskType.Charge);


        //已扣金额
        chargeTask.setPaymentAmount(parameter.getPaymentAmount());


        //扣款状态
        chargeTask.setState(task.getState());

        //扣款的执行时间
        chargeTask.setTime(task.getEffectTime());

        //任务id
        chargeTask.setTaskId(taskId);

        List<HuaXiaEnterpriseAutoChargeContractDetails.ChargeJournal> chargeJournals = new ArrayList<>();
        //查询扣款流水
        List<HuaXiaEnterpriseTaskJournal> huaXiaEnterpriseTaskJournals = this.huaXiaEnterpriseTaskJournalDao.findTop10ByHuaXiaEnterpriseTaskOrderByCreateTimeDesc(task).stream().sorted((t1, t2) -> {
            return t1.getCreateTime() > t1.getCreateTime() ? 1 : -1;
        }).collect(Collectors.toList());


        huaXiaEnterpriseTaskJournals.forEach((journal) -> {
            HuaXiaEnterpriseAutoChargeContractDetails.ChargeJournal chargeJournal = new HuaXiaEnterpriseAutoChargeContractDetails.ChargeJournal();
            long amount = Long.valueOf(String.valueOf(journal.getItems().get("amount")));
            chargeJournal.setAmount(amount);
            chargeJournal.setTime(journal.getCreateTime());
            chargeJournal.setState(journal.getState());
            chargeJournals.add(chargeJournal);
        });


        chargeTask.setJournals(chargeJournals);
        return chargeTask;
    }

    /**
     * 检查开户手机号
     *
     * @param phone
     * @return
     */
    public ResultContent<Boolean> checkOpenAccount(String phone) {
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeContractDao.existsByUserAccount_UserMobile(phone));
    }

    /**
     * 发送绑定的短信验证码
     *
     * @param phone
     * @return
     */
    public ResultContent<String> sendBindMessageNo(String phone) {
        UserMobileValidate userMobileValidate = new UserMobileValidate();
        String code = String.valueOf(RandomUtil.getRandom(100000, 999999));
        List<String> number = new ArrayList<>();
        number.add(phone);
        Map<String, Object> content = new HashMap<>();
        content.put("code", code);
        Map<String, Object> param = new HashMap<>();
        param.put("templateId", huaxiaConf.getTemplateId());
        param.put("number", number);
        param.put("messageType", "Sms");
        param.put("content", content);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(JsonUtil.toJson(param), headers);
        Object result = restTemplate.postForEntity("http://PUSHCENTER/manager/push/sendMessage", entity, String.class);
        userMobileValidate.setPhone(phone);
        userMobileValidate.setCode(code);
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.setTimeInMillis(this.dbHelper.getTime());
        //设置30分钟后过期
        beforeTime.add(Calendar.MINUTE, 30);
        userMobileValidate.setTtl(new Date(beforeTime.getTimeInMillis()));
        userMobileValidateDao.save(userMobileValidate);
        return ResultContent.buildContent(userMobileValidate.getId());
    }

    /**
     * 校验短信验证码
     *
     * @param sessionId
     * @param messageNo
     * @return
     */
    public boolean validateMessageNo(String sessionId, String messageNo) {
        //短信验证码，一次消费，防止暴破
        return userMobileValidateDao.removeByIdAndCode(sessionId, messageNo) > 0;
    }

    public Map<String, Long> statisticsBySuperAdmin(Long startTime, Long endTime) {

        return Map.of("payOrderNum", huaXiaEnterpriseAutoChargeContractDao.countContract(startTime, endTime, null),
                "freezeAmount", huaXiaEnterpriseTaskDao.statisticsAmount(startTime, endTime, TaskType.Freeze),
                "chargeAmount", huaXiaEnterpriseTaskDao.statisticsAmount(startTime, endTime, TaskType.Charge));
    }

    public Map<String, Long> statisticsByAdmin(Long startTime, Long endTime,String epId) {
        return Map.of("payOrderNum", huaXiaEnterpriseAutoChargeContractDao.countContract(startTime, endTime, epId));
    }


}
