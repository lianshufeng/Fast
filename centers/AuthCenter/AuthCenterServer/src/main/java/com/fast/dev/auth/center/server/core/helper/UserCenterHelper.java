package com.fast.dev.auth.center.server.core.helper;

import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.UserRegisterModel;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
public class UserCenterHelper {


    @Autowired
    private RemoteUserCenterService userCenterService;


    /**
     * 通过request取用户id，无缓存
     *
     * @param request
     * @return
     */
    public String getUid(HttpServletRequest request) {
        //参数uid
        String uid = request.getParameter("uid");
        if (StringUtils.hasText(uid)) {
            return uid;
        }
        //参数phone
        String phone = request.getParameter("phone");
        if (StringUtils.hasText(phone)) {
            BaseUserModel baseUserModel = this.userCenterService.queryByLoginName(UserLoginType.Phone, phone);
            if (baseUserModel != null && StringUtils.hasText(baseUserModel.getId())) {
                return baseUserModel.getId();
            } else {
                //如果手机不存在则自动创建一个
                UserRegisterModel userRegisterModel = this.userCenterService.addUser(UserLoginType.Phone, phone, UUID.randomUUID().toString());
                return userRegisterModel.getUid();
            }
        }
        return null;
    }


}
