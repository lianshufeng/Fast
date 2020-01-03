package com.fast.dev.user.auth.plugins;

import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.event.RoleUpdateEvent;
import com.fast.dev.user.auth.helper.UserTokenCacheStreamHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 清空用户令牌的插件
 */

@Log
@Component
public class CleanUserTokenPlugin implements ApplicationListener<RoleUpdateEvent> {


    @Autowired
    private UserTokenCacheStreamHelper userTokenCacheStreamHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;


    //匹配清空用户令牌的方法
    private Set<String> cleanAllUserTokenCacheFromMethod = new HashSet<String>() {{
        add("updateRole");
        add("removeRole");

        add("setRoleParent");
        add("removeRoleParent");

        add("setRoleAuth");
        add("setRoleIdentity");
    }};


    private Set<String> cleanUserTokenCacheFromMethod = new HashSet<String>() {{
        add("addUserRole");
        add("removeUserRole");
    }};


    @Override
    public void onApplicationEvent(RoleUpdateEvent event) {
        RoleUpdateEvent.Source source = event.getSource();
        if (source == null) {
            return;
        }


        //方法执行后
        if (source.getCallType().equals(RoleUpdateEvent.CallType.After)) {


            //判断是否需要清空所有的用户令牌
            if (cleanAllUserTokenCacheFromMethod.contains(source.getMethodName())) {
                //通知清除所有的用户令牌
                cleanAllUserToKen(source.getRet());
            }


            //清空用户令牌
            if (cleanUserTokenCacheFromMethod.contains(source.getMethodName())) {
                if (source.getParm().length > 1) {
                    String[] userIds = (String[]) source.getParm()[1];
                    //通知清除用户令牌的缓存
                    cleanUserTokenCache(userIds);
                }
            }


        }


    }


    /**
     * 清空所有的用户令牌
     *
     * @param expression
     */
    private void cleanAllUserToKen(Object expression) {
        if (expression instanceof Boolean && (boolean) expression) {
            this.userTokenCacheStreamHelper.cleanAll();
        }
    }


    /**
     * 清除指定用户的缓存信息
     *
     * @param userId
     */
    private void cleanUserTokenCache(String... userId) {
        //过滤空字符串
        if (userId == null || userId.length == 0) {
            return;
        }
        Criteria criteria = EntityObjectUtil.createQueryBatch("_id", userId);
        Set<String> uids = this.mongoTemplate.find(new Query(criteria), this.userRoleAuthConf.getUserEntityCls()).stream().map((it) -> {
            return ((User) it).getUid();
        }).collect(Collectors.toSet());
        log.info("清空用户: " + uids);
        this.userTokenCacheStreamHelper.cleanUser(uids.toArray(new String[0]));
    }


}
