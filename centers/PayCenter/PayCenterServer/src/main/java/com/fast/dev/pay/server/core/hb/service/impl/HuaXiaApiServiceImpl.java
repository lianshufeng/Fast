package com.fast.dev.pay.server.core.hb.service.impl;

import com.fast.dev.data.mongo.helper.TransactionHelper;
import com.fast.dev.pay.server.core.dao.FileStoreDao;
import com.fast.dev.pay.server.core.hb.aop.hbapi.HuaXiaEnterpriseHuaXiaApiServiceHelper;
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskJournalDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.model.*;
import com.fast.dev.pay.server.core.hb.model.req.*;
import com.fast.dev.pay.server.core.hb.model.resp.*;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseFreezeTaskParameter;
import com.fast.dev.pay.server.core.hb.service.HBApiService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaApiService;
import com.fast.dev.pay.server.core.hb.type.BizType;
import com.fast.dev.pay.server.core.hb.type.BussCode;
import com.fast.dev.pay.server.core.hb.type.Status;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import com.fast.dev.ucenter.core.util.RandomUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class HuaXiaApiServiceImpl extends HuaXiaApiService {


    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private HuaXiaEnterpriseTaskJournalDao huaXiaEnterpriseTaskJournalDao;

    @Autowired
    private FileStoreDao fileStoreDao;

    @Autowired
    private HBApiService hbApiService;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private HuaxiaConf huaxiaConf;

    @Autowired
    private HuaXiaEnterpriseHuaXiaApiServiceHelper huaXiaEnterpriseHuaXiaApiServiceHelper;


    @Override
    public OpenAcctResp sendMessageNo(HuaXiaEnterpriseAccount enterpriseAccount, String phone) {

        //证书信息
        HuaXiaEnterpriseCertModel certModel = enterpriseAccount.getCertModel();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String certFileId = certModel.getCertFileId();
        fileStoreDao.read(certFileId, outputStream);
        byte[] certBin = outputStream.toByteArray();
        String password = certModel.getStorePassWord();

        OpenAcctReq req = new OpenAcctReq();
        
        
        //必要参数
        req.setTradeNo(huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
        req.setPcsDate(huaXiaEnterpriseHuaXiaApiServiceHelper.getPcsDate());
        req.setAppid(enterpriseAccount.getAppid());
        req.setMchtId(enterpriseAccount.getMchtId());
        req.setMchtNo(enterpriseAccount.getMchtNo());
        req.setMac(getMac());
        req.setOccurAdd(huaxiaConf.getOccurAdd());
        //业务参数
        req.setUserMobile(phone);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(req);
        OpenAcctResp resp = hbApiService.openAcct(req, certBin, password);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(resp);
        return resp;
    }

    @Override
    public ApiResult openAccount(SuperAutoChargeContractModel model) {
        log.info("HuaXiaApi : {}", "openAccount");
        HuaXiaUserAccountModel userAccount = model.getUserAccount();
        //证书信息
        HuaXiaEnterpriseAccount enterpriseAccount = huaXiaEnterpriseAccountDao.findByEpId(model.getEpId());
        HuaXiaEnterpriseCertModel certModel = enterpriseAccount.getCertModel();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String certFileId = certModel.getCertFileId();
        fileStoreDao.read(certFileId, outputStream);
        byte[] certBin = outputStream.toByteArray();
        String password = certModel.getStorePassWord();
        //构建请求参数
        ConfrimOpenAcctReq req = new ConfrimOpenAcctReq();
        BeanUtils.copyProperties(userAccount, req, "jobs", "idType", "accountType");
        //必要参数
        req.setTradeNo(huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
        req.setPcsDate(huaXiaEnterpriseHuaXiaApiServiceHelper.getPcsDate());
        req.setAppid(enterpriseAccount.getAppid());
        req.setMchtId(enterpriseAccount.getMchtId());
        req.setMchtNo(enterpriseAccount.getMchtNo());
        req.setMac(getMac());
        req.setOccurAdd(huaxiaConf.getOccurAdd());
        //业务参数
        req.setOrgPcsDate(model.getTxnDate());
        req.setOrgTradeNo(model.getOrgTradeNo());
        req.setMessageNo(model.getMessageNo());
        req.setIdType(userAccount.getIdType().getCode());
        req.setAccountType("2");
        req.setJobs(String.valueOf(userAccount.getJobs().getCode()));
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(req);
        ConfrimOpenAcctResp resp = hbApiService.confrimOpenAcct(req, certBin, password);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(resp);
        if (resp.getStatus() != Status.SUCCESS) {
            return ApiResult.fail(resp.getRetMsg());
        }
        //返回的开户信息
        HuaXiaEnterpriseUserOpenAccountInfoModel userOpenAccountInfo = new HuaXiaEnterpriseUserOpenAccountInfoModel();
        BeanUtils.copyProperties(resp, userOpenAccountInfo);
        model.setUserOpenAccountInfo(userOpenAccountInfo);
        return ApiResult.success();
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRES_NEW)
    public ApiResult freezeTask(HuaXiaEnterpriseTask task) {
        log.info("HuaXiaApi : {}", "freezeTask");

        //企业信息
        HuaXiaEnterpriseAccount enterpriseAccount = task.getHuaXiaEnterpriseAccount();
        long startTime = task.getLastExecuteTime();

        //证书信息
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String certFileId = enterpriseAccount.getCertModel().getCertFileId();
        fileStoreDao.read(certFileId, outputStream);
        byte[] certBin = outputStream.toByteArray();
        String password = enterpriseAccount.getCertModel().getStorePassWord();

        //用户信息
        HuaXiaUserAccountModel userAccount = task.getContract().getUserAccount();
        //用户开户信息
        HuaXiaEnterpriseUserOpenAccountInfoModel userOpenAccountInfo = task.getContract().getUserOpenAccountInfo();
        //构建请求参数
        FreezeAcctReq req = new FreezeAcctReq();
        req.setTradeNo(huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
        req.setPcsDate(huaXiaEnterpriseHuaXiaApiServiceHelper.getPcsDate());
        req.setAppid(enterpriseAccount.getAppid());
        req.setMchtId(enterpriseAccount.getMchtId());
        req.setMchtNo(enterpriseAccount.getMchtNo());
        req.setMac(getMac());
        req.setOccurAdd(huaxiaConf.getOccurAdd());
        //业务参数
        req.setCardNo(userOpenAccountInfo.getCardNo());
        req.setActNm(userAccount.getUserName());
        req.setPhoneNo(userAccount.getUserMobile());
        req.setFrzAmt(getFrzAmt(task.getContract().getAutoCharge().getAutoChargeInfos()));
        AutoChargeInfo[] autoChargeInfos = Arrays.copyOf(task.getContract().getAutoCharge().getAutoChargeInfos(),task.getContract().getAutoCharge().getAutoChargeInfos().length);
        req.setHlExDt(getHlExDt(startTime, autoChargeInfos));
        req.setIdNo(userAccount.getIdNo());
        req.setIdType(userAccount.getIdType().getCode());
        req.setType("1");
        req.setFrzReason("冻结");
        req.setInternetPresence(huaxiaConf.getInternetPresence());
        req.setChannelName("01");
        req.setBussCode(BussCode.OtherConsumption.getCode());
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(req);
        FreezeAcctResp resp = hbApiService.freezeAcct(req, certBin, password);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(resp);

        if (resp.getStatus() != Status.SUCCESS) {
            return ApiResult.fail(resp.getRetMsg());
        }
        //需要储存的返回参数
        HuaXiaEnterpriseFreezeTaskParameter parameter = task.readParameter(TaskType.Freeze);
        BeanUtils.copyProperties(resp, parameter);
        this.huaXiaEnterpriseTaskDao.updateParameter(task.getId(), parameter);
        return ApiResult.success();
    }

    @Override
    public ApiResult chargeTask(HuaXiaEnterpriseTask task, HuaXiaEnterpriseTaskJournal taskJournal, long amount) {
        log.info("HuaXiaApi : {} -> {}", "chargeTask", amount);

        HuaXiaEnterpriseAutoChargeContract contract = task.getContract();
        //获取扣款任务之前的冻结任务
        List<HuaXiaEnterpriseTask> freezeTask = huaXiaEnterpriseTaskDao.findByContractAndType(contract, TaskType.Freeze);

        HuaXiaEnterpriseTask huaXiaEnterpriseTask = freezeTask.get(0);

        HuaXiaEnterpriseFreezeTaskParameter parameter = huaXiaEnterpriseTask.readParameter(TaskType.Freeze);

        //企业信息
        HuaXiaEnterpriseAccount enterpriseAccount = task.getHuaXiaEnterpriseAccount();

        //用户信息
        HuaXiaUserAccountModel userAccount = task.getContract().getUserAccount();
        //用户开户信息
        HuaXiaEnterpriseUserOpenAccountInfoModel userOpenAccountInfo = task.getContract().getUserOpenAccountInfo();
        //证书信息
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String certFileId = enterpriseAccount.getCertModel().getCertFileId();
        fileStoreDao.read(certFileId, outputStream);
        byte[] certBin = outputStream.toByteArray();
        String password = enterpriseAccount.getCertModel().getStorePassWord();

        //构建请求参数
        ThawPayRefundReq req = new ThawPayRefundReq();
        req.setTradeNo(huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
        req.setPcsDate(huaXiaEnterpriseHuaXiaApiServiceHelper.getPcsDate());
        req.setAppid(enterpriseAccount.getAppid());
        req.setMchtId(enterpriseAccount.getMchtId());
        req.setMchtNo(enterpriseAccount.getMchtNo());
        req.setMac(getMac());
        req.setOccurAdd(huaxiaConf.getOccurAdd());
        //业务参数
        req.setCardNo(userOpenAccountInfo.getCardNo());
        req.setActNm(userAccount.getUserName());
        req.setPhoneNO(userAccount.getUserMobile());
        req.setIdNo(userAccount.getIdNo());
        req.setIdType(userAccount.getIdType().getCode());
        req.setOrgPcsDate(parameter.getTxnDate());
        req.setOrgTradeNo(parameter.getOrgTradeNo());
        req.setBussCode(BussCode.OtherConsumption.getCode());
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.divide(new BigDecimal(100));
        req.setPayNote("定期费用扣除");
        req.setChannelName("01");
        req.setInternetPresence(huaxiaConf.getInternetPresence());

        req.setTotalAmt(getFrzAmt(task.getContract().getAutoCharge().getAutoChargeInfos()));
        req.setType("1");
        req.setAmt(bigDecimal.toString());
        if (req.getAmt().equals(req.getTotalAmt())){
            req.setTotalNum(String.valueOf(1));
        } else {
            req.setTotalNum(String.valueOf(9));
        }
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(req);
        ThawPayRefundResp resp = hbApiService.thawPayRefund(req, certBin, password);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(resp);
        if (resp.getStatus() != Status.SUCCESS) {
            return ApiResult.fail(resp.getRetMsg());
        }
        Map<String, Object> items = taskJournal.getItems();
        items.put("orgTradeNo", resp.getOrgTradeNo());
        items.put("txnDate", resp.getTxnDate());
        taskJournal.setItems(items);
        //出储存每次接口返回的值
        this.huaXiaEnterpriseTaskJournalDao.updateTaskJournal(taskJournal);

        return ApiResult.success();
    }

    @Override
    public ApiResult checkChargeTask(HuaXiaEnterpriseTask task) {
        log.info("HuaXiaApi : {} -> {}", "checkChargeTask", task);

        HuaXiaEnterpriseCheckChargeTaskParameter parameter = task.readParameter(TaskType.CheckCharge);
        //获取流水
        String taskJournalId = parameter.getTaskJournalId();
        HuaXiaEnterpriseTaskJournal journal = huaXiaEnterpriseTaskJournalDao.findTop1ById(taskJournalId);

        //企业信息
        HuaXiaEnterpriseAccount enterpriseAccount = task.getHuaXiaEnterpriseAccount();
        //证书信息
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String certFileId = enterpriseAccount.getCertModel().getCertFileId();
        fileStoreDao.read(certFileId, outputStream);
        byte[] certBin = outputStream.toByteArray();
        String password = enterpriseAccount.getCertModel().getStorePassWord();
        //构建请求参数
        QueryTransResultReq req = new QueryTransResultReq();
        req.setTradeNo(huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
        req.setPcsDate(huaXiaEnterpriseHuaXiaApiServiceHelper.getPcsDate());
        req.setAppid(enterpriseAccount.getAppid());
        req.setMchtId(enterpriseAccount.getMchtId());
        req.setMchtNo(enterpriseAccount.getMchtNo());
        req.setMac(getMac());
        req.setOccurAdd(huaxiaConf.getOccurAdd());
        //业务参数
        req.setBizType(BizType.THAWPAY);
        Map<String, Object> items = journal.getItems();
        req.setOrgTradeNo((String) items.get("orgTradeNo"));
        req.setOrgPcsDate((String) items.get("txnDate"));
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(req);
        QueryTransResultResp resp = hbApiService.queryTransResult(req, certBin, password);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(resp);
        if (!resp.getOrgStatus().equals(Status.SUCCESS.name())) {
            return ApiResult.fail(resp.getOrgRetMsg());
        }
        return ApiResult.success();
    }

    static String getTradeNo() {
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return formatter.format(zdt) + RandomUtil.randInt(10000, 99999);
    }

    static String getPcsDate() {
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return formatter.format(zdt);
    }


    @SneakyThrows
    static String getMac() {
        InetAddress ia = InetAddress.getLocalHost();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    static String getFrzAmt(AutoChargeInfo[] autoChargeInfos) {
        long totalAmount = 0L;
        for (AutoChargeInfo chargeInfo : autoChargeInfos) {
            totalAmount = totalAmount + chargeInfo.getAmount();
        }
        BigDecimal bigDecimal = new BigDecimal(totalAmount);
        bigDecimal = bigDecimal.divide(new BigDecimal(100));
        return bigDecimal.toString();
    }

    static String getHlExDt(long startTime, AutoChargeInfo[] autoChargeInfos) {

        List<AutoChargeInfo> autoChargeInfoList = Arrays.asList(autoChargeInfos);
        Collections.sort(autoChargeInfoList, (info1, info2) -> {
            return info1.getTime() < info2.getTime() ? 1 : -1;
        });
        long hlExDt = Long.valueOf(autoChargeInfoList.get(0).getTime()) * 86400000L  + startTime;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(hlExDt);
        c.add(Calendar.YEAR, 1);
        return formatter.format(c.getTime());
    }

    @SneakyThrows
    byte[] getCert() {
        FileInputStream jks = new FileInputStream("D:/hb/kayak.jks");
        byte[] byt = new byte[jks.available()];
        jks.read(byt);
        return byt;
    }
}
