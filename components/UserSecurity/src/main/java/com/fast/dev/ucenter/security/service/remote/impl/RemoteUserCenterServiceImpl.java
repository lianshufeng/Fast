package com.fast.dev.ucenter.security.service.remote.impl;

import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.security.conf.UserSecurityConf;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.*;

public class RemoteUserCenterServiceImpl implements RemoteUserCenterService {

    @Autowired
    private UserSecurityConf userSecurityConf;

    @Autowired
    private RestTemplate restTemplate;


    //支持文本的类型
    private final static Set<Class> SupportTextType = new HashSet<Class>() {
        {
            add(String.class);

            add(int.class);
            add(Integer.class);

            add(double.class);
            add(Double.class);

            add(long.class);
            add(Long.class);

            add(float.class);
            add(Float.class);

            add(boolean.class);
            add(Boolean.class);
        }
    };


    @Override
    public UserTokenModel queryByUserToken(String token) {
        UserTokenModel userTokenModel = post(UserTokenModel.class, "manager/queryUserToken", new HashMap<String, Object>() {{
            put("token", token);
        }});
        return userTokenModel;

    }

    @Override
    public boolean logout(String token) {
        return post(boolean.class, "manager/logout", new HashMap<String, Object>() {{
            put("token", token);
        }});
    }

    @Override
    public BaseUserModel queryUserId(String uid) {
        return post(BaseUserModel.class, "manager/queryUserId", new HashMap<String, Object>() {{
            put("uid", uid);
        }});
    }

    @Override
    public BaseUserModel queryByLoginName(UserLoginType loginType, String loginName) {
        return post(BaseUserModel.class, "manager/queryByLoginName", new HashMap<String, Object>() {{
            put("loginType", loginType);
            put("loginName", loginName);
        }});
    }

    @Override
    public UserRegisterModel addUser(UserLoginType loginType, String loginName, String passWord) {
        return post(UserRegisterModel.class, "manager/addUser", new HashMap<String, Object>() {{
            put("loginType", loginType);
            put("loginName", loginName);
            put("passWord", passWord);
        }});
    }

    @Override
    public UserTokenModel login(UserLoginType loginType, String loginName, String passWord, Long expireTime, TokenEnvironment env) {
        return post(UserTokenModel.class, "manager/login", new HashMap<String, Object>() {{
            put("loginType", loginType);
            put("loginName", loginName);
            put("passWord", passWord);
            put("expireTime", expireTime);
        }});
    }

    @Override
    public UserTokenModel createToken(String uid, Long expireTime, TokenEnvironment env) {
        return post(UserTokenModel.class, "manager/createToken", new HashMap<String, Object>() {{
            put("uid", uid);
            put("expireTime", expireTime);
        }});
    }

    @Override
    public UserRegisterModel insertBaseUser(UserLoginType loginType, String loginName, String salt, String passWord, PassWordEncodeType encodeType) {
        return post(UserRegisterModel.class, "manager/insertBaseUser", new HashMap<String, Object>() {{
            put("loginType", loginType);
            put("loginName", loginName);
            put("salt", salt);
            put("passWord", passWord);
            put("encodeType", encodeType);

        }});
    }

    @Override
    public BaseUserModel updateLoginName(String uid, UserLoginType loginType, String loginName) {
        return post(BaseUserModel.class, "manager/updateLoginName", new HashMap<String, Object>() {{
            put("uid", uid);
            put("loginType", loginType);
            put("loginName", loginName);
        }});
    }

    @Override
    public TokenState setUserPassWord(String uid, String passWord) {
        return post(TokenState.class, "manager/setUserPassWord", new HashMap<String, Object>() {{
            put("uid", uid);
            put("passWord", passWord);
        }});
    }

    @Override
    public long cleanUserToken(String uid, String[] ignoreUToken) {
        return post(long.class, "manager/cleanUserToken", new HashMap<String, Object>() {{
            put("uid", uid);
            put("ignoreUToken", ignoreUToken);
        }});
    }

    @Override
    public Page<BaseUserLogModel> listUserUpdateLoginName(String uid, Pageable pageable) {
        return post(Page.class, "manager/listUserUpdateLoginName", new HashMap<String, Object>() {{
            put("uid", uid);
            put("page", pageable.getPageSize());
            put("size", pageable.getPageSize());
            put("sort", pageable.getSort());
        }});
    }

    @Override
    public boolean disable(String[] uid, long time, String reason) {
        return post(Boolean.class, "manager/disable", new HashMap<String, Object>() {{
            put("uid", uid);
            put("time", time);
            put("reason", reason);
        }});
    }

    @Override
    public boolean enable(String[] uid) {
        return post(Boolean.class, "manager/enable", new HashMap<String, Object>() {{
            put("uid", uid);
        }});
    }


    public <T> T post(Class<T> responseType, String uri, Map<String, Object> parm) {
        String url = "http://" + this.userSecurityConf.getUcenterAppName() + "/ucenter/" + uri;
        //表单提交
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //参数
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : parm.entrySet()) {
            setParmMap(map, entry.getKey(), entry.getValue());
        }
        return this.restTemplate.postForEntity(url, new HttpEntity<MultiValueMap<String, String>>(map, headers), responseType).getBody();
    }


    private void setParmMap(final MultiValueMap<String, String> formMap, String key, Object value) {
        //过滤为null的参数
        if (value == null) {
            return;
        }
        if (SupportTextType.contains(value.getClass()) || value.getClass().isEnum()) {
            List<String> values = formMap.get(key);
            if (values == null) {
                values = new ArrayList<>();
                formMap.put(key, values);
            }
            values.add(String.valueOf(value));
        } else if (value.getClass().isArray()) {
            //数组判断
            Object[] valObj = (Object[]) value;
            for (Object o : valObj) {
                setParmMap(formMap, key, o);
            }
        } else if (value instanceof Map) {
            //map处理
            Map<String, Object> valMap = (Map) value;
            for (Map.Entry<String, Object> entry : valMap.entrySet()) {
                setParmMap(formMap, entry.getKey(), entry.getValue());
            }
        } else {
            for (Field field : value.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    setParmMap(formMap, key, field.get(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
