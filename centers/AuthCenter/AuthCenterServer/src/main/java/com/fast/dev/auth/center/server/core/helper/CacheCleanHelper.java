package com.fast.dev.auth.center.server.core.helper;

import com.fast.dev.auth.center.server.core.dao.FamilyDao;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import com.fast.dev.ucenter.core.helper.UserCenterOuputStreamHelper;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.type.UserMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class CacheCleanHelper {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private FamilyDao familyDao;

    @Autowired
    private UserCenterOuputStreamHelper userCenterOuputStreamHelper;


    //线程池
    ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @PreDestroy
    private void shutdown() {
        threadPool.shutdownNow();
    }


    /**
     * 清空家庭组的用户缓存
     *
     * @param id
     */
    public void addFamily(final String id) {
        Family family = this.familyDao.findTop1ById(id);
        if (family == null) {
            return;
        }
        this.pushCleanUserCache(family.getMember().parallelStream().map((member) -> {
            return member.getUid();
        }).collect(Collectors.toSet()).toArray(new String[0]));
    }


    /**
     * 添加企业
     *
     * @param id
     */
    public void addEnterprise(final String id) {
        threadPool.execute(() -> {
            List<Role> roles = roleDao.findByEnterprise(Enterprise.build(id));
            if (roles != null) {
                roles.forEach((role) -> {
                    this.addRole(role.getId());
                });
            }
        });
    }


    /**
     * 清空一个角色的缓存
     *
     * @param roleId
     */
    public void addRole(final String roleId) {
        threadPool.execute(() -> {

            List<UserRole> userRoles = roleDao.listRoleUser(roleId);
            if (userRoles == null) {
                return;
            }

            this.pushCleanUserCache(userRoles.parallelStream().map((it) -> {
                return it.getUser().getUid();
            }).collect(Collectors.toSet()).toArray(new String[0]));

        });
    }

    /**
     * 清空一个用户的缓存
     *
     * @param uid
     */
    public void addUser(final String uid) {
        threadPool.execute(() -> {
            pushCleanUserCache(uid);
        });
    }


    /**
     * 清空缓存
     */
    private void pushCleanUserCache(String... uids) {
        if (uids == null) {
            return;
        }
        for (String uid : uids) {
            UserMessage userMessage = new UserMessage();
            userMessage.setType(UserMessageType.CleanCache);
            userMessage.setUid(uid);
            this.userCenterOuputStreamHelper.publish(userMessage);
        }
    }
}
