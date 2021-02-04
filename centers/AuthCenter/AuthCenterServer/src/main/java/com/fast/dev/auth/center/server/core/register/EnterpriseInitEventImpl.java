package com.fast.dev.auth.center.server.core.register;

import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.RoleModel;
import com.fast.dev.auth.client.model.RoleUserModel;
import com.fast.dev.auth.client.model.UserModel;
import com.fast.dev.auth.client.register.EnterpriseInitEvent;
import com.fast.dev.auth.client.type.AuthConstantType;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;

/**
 * 企业初始化事件
 */
public class EnterpriseInitEventImpl implements EnterpriseInitEvent {


    @Override
    public boolean condition(EnterpriseModel enterpriseModel) {
        return !StringUtils.hasText(enterpriseModel.getFlag());
    }


    @Override
    public Collection<RoleUserModel> onCreate(EnterpriseModel enterpriseModel, String ownerUid) {
        // 创建admin角色
        RoleModel adminRole = createAdminRole();

        RoleUserModel roleUserModel = new RoleUserModel();
        BeanUtils.copyProperties(adminRole, roleUserModel);

        //角色中增加用户
        roleUserModel.setUserModel(new HashSet<UserModel>() {{
            add(new UserModel(ownerUid, null));
        }});

        return new HashSet<RoleUserModel>() {{
            add(roleUserModel);
        }};
    }


    /**
     * 创建Admin的角色
     *
     * @return
     */
    private RoleModel createAdminRole() {
        RoleModel roleModel = new RoleModel();

        //角色名
        roleModel.setRoleName(AuthConstantType.Admin.name());

        //权限
        roleModel.setAuth(new HashSet<String>() {{
            add(AuthConstantType.Admin.name());
        }});

        //身份
        roleModel.setIdentity(new HashSet<String>() {{
            add(AuthConstantType.Admin.name());
        }});

        //备注
        roleModel.setRemark(AuthConstantType.Admin.getRoleName());

        return roleModel;
    }


}
