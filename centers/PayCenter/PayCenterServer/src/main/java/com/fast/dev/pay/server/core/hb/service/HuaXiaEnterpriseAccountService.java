package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.response.ResponseUtil;
import com.fast.dev.core.util.token.TokenUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.dao.FileStoreDao;
import com.fast.dev.pay.server.core.dao.IncTableDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.helper.CertHelper;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseCertModel;
import com.fast.dev.pay.server.core.hb.util.RandomUtil;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class HuaXiaEnterpriseAccountService {

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private FileStoreDao fileStoreDao;

    @Autowired
    private CertHelper certHelper;

    @Autowired
    private IncTableDao incTableDao;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取华夏企业账户,未成功注册到我们企业的列表
     *
     * @return
     */
    public Page<HuaXiaEnterpriseAccountModel> list(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.huaXiaEnterpriseAccountDao.list(huaXiaEnterpriseAccountModel, pageable), (account) -> {
            return toModel(account);
        });
    }


    /**
     * 获取当前企业信息
     *
     * @param epId
     * @return
     */
    public HuaXiaEnterpriseAccountModel get(String epId) {
        return toModel(this.huaXiaEnterpriseAccountDao.findByEpId(epId));
    }


    /**
     * 下载公钥证书
     */
    public void downPublicCert(HttpServletRequest request, HttpServletResponse response, String id) {
        this.huaXiaEnterpriseAccountDao.findById(id).ifPresentOrElse((account) -> {

            HuaXiaEnterpriseCertModel certModel = account.getCertModel();

            //读取私钥
            ByteArrayOutputStream privateKeyOutputStream = new ByteArrayOutputStream();
            this.fileStoreDao.read(certModel.getCertFileId(), privateKeyOutputStream);
            ByteArrayInputStream privateKeyInputStream = new ByteArrayInputStream(privateKeyOutputStream.toByteArray());

            //生成公约
            ByteArrayOutputStream publicKeyOutputStream = new ByteArrayOutputStream();
            certHelper.makePublicCert(privateKeyInputStream, certModel.getStorePassWord(), certModel.getAlias(), publicKeyOutputStream);
            ByteArrayInputStream publicKeyInputStream = new ByteArrayInputStream(publicKeyOutputStream.toByteArray());

            //写出数据
            ResponseUtil.writeStream(request, response, publicKeyInputStream, publicKeyOutputStream.toByteArray().length, "p10");

        }, () -> {
            response.setStatus(404);
        });
    }


    /**
     * 更新企业
     *
     * @param huaXiaEnterpriseAccountModel
     * @return
     */
//    @Transactional
    public ResultContent<String> update(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel) {
        //华夏企业账户
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = null;
        if (StringUtils.hasText(huaXiaEnterpriseAccountModel.getId())) {
            huaXiaEnterpriseAccount = this.huaXiaEnterpriseAccountDao.findTop1ById(huaXiaEnterpriseAccountModel.getId());
        }
        if (huaXiaEnterpriseAccount == null) {
            huaXiaEnterpriseAccount = new HuaXiaEnterpriseAccount();
        }

        Set<String> ignoreProperties = new HashSet<>() {{
            add("epId");
            add("id");
        }};
        BeanUtil.getNullPropertyNames(huaXiaEnterpriseAccountModel, ignoreProperties);
        BeanUtils.copyProperties(huaXiaEnterpriseAccountModel, huaXiaEnterpriseAccount, ignoreProperties.toArray(new String[0]));


        //初始化企业编码
        if (!StringUtils.hasText(huaXiaEnterpriseAccount.getCode())) {
            resetCode(huaXiaEnterpriseAccount);
        }

        //如果没有证书则初始一个证书
        if (huaXiaEnterpriseAccount.getCertModel() == null) {
            resetCert(huaXiaEnterpriseAccount);
        }

        //重置企业工作时间
        if (huaXiaEnterpriseAccount.getWorkTime() == null) {
            resetWorkTime(huaXiaEnterpriseAccount);
        }

        //设置工作时间的表达式
        huaXiaEnterpriseAccount.setCron(workTimeToCron(huaXiaEnterpriseAccount.getWorkTime()));

        //保存修改
        this.huaXiaEnterpriseAccountDao.save(huaXiaEnterpriseAccount);
        return ResultContent.build(StringUtils.hasText(huaXiaEnterpriseAccount.getId()) ? ResultState.Success : ResultState.Fail, huaXiaEnterpriseAccount.getId());
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Transactional
    public ResultState remove(String id) {
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findTop1ById(id);
        if (account == null) {
            return ResultState.Fail;
        }
        //仅能删除没有注册过企业的信息
        if (!StringUtils.hasText(account.getEpId())) {
            //释放证书所占用的文件资源
            HuaXiaEnterpriseCertModel certModel = account.getCertModel();
            if (certModel != null) {
                this.fileStoreDao.delete(certModel.getCertFileId());
            }
            return this.mongoTemplate.remove(account).getDeletedCount() > 0 ? ResultState.Success : ResultState.Fail;
        }
        return ResultState.Fail;
    }


    /**
     * 重置企业证书
     *
     * @param id
     * @return
     */
    @Transactional
    public ResultState resetCert(String id) {
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findTop1ById(id);
        if (account == null) {
            return ResultState.HuaXiaEnterpriseIdError;
        }

        //重置证书
        resetCert(account);
        this.mongoTemplate.save(account);

        return ResultState.Success;
    }


    /**
     * 注册企业
     *
     * @param id
     * @return
     */
    @Transactional
    public ResultState register(String id) {
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findTop1ById(id);
        if (account == null) {
            return ResultState.HuaXiaEnterpriseIdError;
        }
        if (StringUtils.hasText(account.getEpId())) {
            return ResultState.EnterpriseRegisterExist;
        }
        if (!StringUtils.hasText(account.getMchtId())) {
            return ResultState.MchIdNotNull;
        }
        if (!StringUtils.hasText(account.getMchtNo())) {
            return ResultState.MchNoNotNull;
        }
        if (!StringUtils.hasText(account.getAppid())) {
            return ResultState.AppIdNotNull;
        }
        if (!StringUtils.hasText(account.getEnterprisePhone())) {
            return ResultState.PhoneNotNull;
        }
        if (!StringUtils.hasText(account.getEnterpriseName())) {
            return ResultState.EnterpriseNameNotNull;
        }


        //用户中心中查询UID
        String uid = null;
        BaseUserModel baseUserModel = this.remoteUserCenterService.queryByLoginName(UserLoginType.Phone, account.getEnterprisePhone());
        if (baseUserModel == null || !StringUtils.hasText(baseUserModel.getId())) {
            uid = this.remoteUserCenterService.addUser(UserLoginType.Phone, account.getEnterprisePhone(), TokenUtil.create().substring(0, 6)).getUid();
        } else {
            uid = baseUserModel.getId();
        }


        //创建企业
        EnterpriseModel enterpriseModel = new EnterpriseModel();
        enterpriseModel.setName(account.getEnterpriseName());
        enterpriseModel.setRemark("华夏代扣业务创建");
        //注册企业
        com.fast.dev.auth.client.model.ResultContent<String> resultContent = enterpriseService.add(enterpriseModel, uid);
        //处理注册企业失败的情况
        if (resultContent.getState() != com.fast.dev.auth.client.type.ResultState.Success) {
            return ResultState.valueOf(resultContent.getState().name());
        }

        //设置注册的企业id
        account.setEpId(resultContent.getContent());

        this.dbHelper.updateTime(account);
        this.huaXiaEnterpriseAccountDao.save(account);

        return ResultState.Success;
    }


    /**
     * 重置企业工作时间
     *
     * @param huaXiaEnterpriseAccount
     */
    private void resetWorkTime(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount) {
        //默认从8点到20点,每个半小时
        int start = 8;
        int end = 20;
        //间隔30分钟
        int interval = 20;
        //计算总共档次
        int rCount = (end - start) * (60 / interval);
        //随机的时间
        int workTimePoint = 8 * 60 + RandomUtil.getRandom(0, rCount) * interval;
        //8点+随机值
        huaXiaEnterpriseAccount.setWorkTime(workTimePoint);
    }


    /**
     * 重置企业证书
     */
    @SneakyThrows
    private void resetCert(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount) {
        HuaXiaEnterpriseCertModel certModel = huaXiaEnterpriseAccount.getCertModel();
        if (certModel == null) {
            certModel = new HuaXiaEnterpriseCertModel();
        } else if (StringUtils.hasText(certModel.getCertFileId())) {
            //如果之前有数据，则先删除掉
            this.fileStoreDao.delete(certModel.getCertFileId());
        }

        certModel.setStorePassWord(TokenUtil.create().substring(0, 6));
        certModel.setAlias("cert");
        certModel.setOrganizational(huaXiaEnterpriseAccount.getEnterpriseName());
        certModel.setValidity(365 * 99);


        //生产私钥
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        certHelper.makePrivateCert(certModel, outputStream);

        @Cleanup ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        certModel.setCertFileId(this.fileStoreDao.write(byteArrayInputStream, huaXiaEnterpriseAccount.getEnterpriseName() + ".jks"));


        huaXiaEnterpriseAccount.setCertModel(certModel);
    }


    /**
     * 重置企业编码
     *
     * @param huaXiaEnterpriseAccount
     */
    private void resetCode(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount) {
        long number = this.incTableDao.inc("HuaXiaEnterpriseAccountCode");
        String code = String.valueOf(number);
        while (code.length() < 7) {
            code = "0" + code;
        }
        code = "c" + code;
        huaXiaEnterpriseAccount.setCode(code);
    }


    /**
     * 转换模型
     *
     * @param huaXiaEnterpriseAccount
     * @return
     */
    private static HuaXiaEnterpriseAccountModel toModel(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount) {
        HuaXiaEnterpriseAccountModel model = new HuaXiaEnterpriseAccountModel();
        BeanUtils.copyProperties(huaXiaEnterpriseAccount, model, "certModel");
        return model;
    }

    /**
     * 工作时间转换为表达式
     *
     * @param workTime
     * @return
     */
    private String workTimeToCron(int workTime) {
        String cron = "0 %s %s * * ?";
        int h = workTime / 60;
        int m = workTime - h * 60;
        return String.format(cron, String.valueOf(m), String.valueOf(h));
    }


}

