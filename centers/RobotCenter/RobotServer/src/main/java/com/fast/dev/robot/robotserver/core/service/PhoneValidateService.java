package com.fast.dev.robot.robotserver.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.type.MessageType;
import com.fast.dev.robot.robotserver.core.dao.PhoneCodeRecordDao;
import com.fast.dev.robot.robotserver.core.domain.PhoneCodeRecord;
import com.fast.dev.robot.robotserver.core.model.ValidatePhoneTokenRet;
import com.fast.dev.robot.robotserver.core.model.VerifyPhoneModel;
import com.fast.dev.robot.robotserver.core.type.PhoneCodeType;
import com.fast.dev.robot.robotserver.core.util.RandomCodeUtil;
import com.fast.dev.robot.service.model.PhoneCodeRet;
import com.fast.dev.robot.service.type.ValidateType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class PhoneValidateService {

    @Autowired
    private PhoneCodeRecordDao phoneCodeRecordDao;

    @Autowired
    private SendPushMessageHelper sendPushMessageHelper;

    @Autowired
    private DBHelper dbHelper;


    /**
     * 发送给手机验证码
     *
     * @param verifyPhoneModel
     * @return
     */
    @Transactional
    public ValidatePhoneTokenRet sendPhoneCode(VerifyPhoneModel verifyPhoneModel) {
        boolean isNumber = (verifyPhoneModel.getPhoneCodeType() == PhoneCodeType.Number);
        //生成随机码
        String code = RandomCodeUtil.getCode(isNumber, verifyPhoneModel.getCodeSize());

        //入库
        PhoneCodeRecord phoneCodeRecord = new PhoneCodeRecord();
        phoneCodeRecord.setPhone(verifyPhoneModel.getPhone());
        phoneCodeRecord.setCode(code);
        phoneCodeRecord.setToken(RandomCodeUtil.nextToken(32));
        phoneCodeRecord.setExpireTime(this.dbHelper.getTime() + verifyPhoneModel.getTimeOut());
        phoneCodeRecord.setTryCount(verifyPhoneModel.getTryCount());
        phoneCodeRecordDao.save(phoneCodeRecord);

        //发送短信
        pushMessage(code, verifyPhoneModel);

        //返回对象
        ValidatePhoneTokenRet validatePhoneTokenRet = new ValidatePhoneTokenRet();
        BeanUtils.copyProperties(phoneCodeRecord, validatePhoneTokenRet);
        return validatePhoneTokenRet;
    }


    /**
     * 验证手机的编码
     */
    public PhoneCodeRet verifyPhoneCode(String token, String code) {
        PhoneCodeRet phoneCodeRet = PhoneCodeRet.builder().token(token).build();
        PhoneCodeRecord phoneCodeRecord = this.phoneCodeRecordDao.queryAndIncTryCount(token);
        if (phoneCodeRecord == null) {
            phoneCodeRet.setValidateType(ValidateType.NotExist);
            return phoneCodeRet;
        }
        //校验验证码是否正确
        phoneCodeRet.setValidateType(phoneCodeRecord.getCode().equals(code) ? ValidateType.Done : ValidateType.Error);
        return phoneCodeRet;
    }


    /**
     * 推送消息
     *
     * @param code
     * @param verifyPhoneModel
     */
    private void pushMessage(String code, VerifyPhoneModel verifyPhoneModel) {

        PlatformMessage platformMessage = new PlatformMessage();
        platformMessage.setTemplateId(verifyPhoneModel.getTemplateId());
        platformMessage.setNumber(new String[]{verifyPhoneModel.getPhone()});
        platformMessage.setMessageType(MessageType.Sms);
        platformMessage.setContent(new HashMap<String, Object>() {{
            put("code", code);
        }});

        sendPushMessageHelper.pushPlatformMessage(platformMessage);
    }


}
