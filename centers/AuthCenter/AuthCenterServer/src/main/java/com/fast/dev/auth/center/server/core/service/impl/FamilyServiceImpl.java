package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.dao.FamilyDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.center.server.core.helper.CacheCleanHelper;
import com.fast.dev.auth.center.server.core.model.FamilyMemberModel;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.FamilyMember;
import com.fast.dev.auth.client.model.FamilyModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.FamilyService;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.ucenter.core.batch.BatchUserService;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryValue;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyDao familyDao;

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private BatchUserService batchUserService;

    @Autowired
    private CacheCleanHelper cacheCleanHelper;

    @Autowired
    private UserDao userDao;


    @Override
    @Transactional
    @UserLog(parameter = {"#familyModel"})
    public ResultContent<String> update(FamilyModel familyModel) {
        Assert.hasText(familyModel.getEpId(), "企业id不能为空");
        Assert.hasText(familyModel.getOwner(), "家庭的拥有者不能为空");
        Assert.notEmpty(familyModel.getMember(), "家庭成员不能为空");
        Assert.state(familyModel.getMember().stream().map((it) -> {
                    return it.getUid();
                }).collect(Collectors.toSet()).contains(familyModel.getOwner())
                , "家庭的拥有者必须在家庭成员中");
        validateFamilyRepeatMember(familyModel.getMember(), "家庭组的成员不能重复");

        final String epId = familyModel.getEpId();
        //企业存在
        if (!this.enterpriseDao.existsById(epId)) {
            return ResultContent.build(ResultState.EnterpriseNotExist);
        }

        //是否新增家庭
        final boolean isInsert = !StringUtils.hasText(familyModel.getId());

        //家庭成员的uid
        final Set<String> member = familyModel.getMemberUid();

        //校验用户id的合法性
        ResultContent<String> validateUserCenterResult = this.validateUserCenter(member);
        if (validateUserCenterResult != null) {
            return validateUserCenterResult;
        }

        //校验企业下是否家庭组成员里是否包含需要更改的用户
        ResultContent<String> validateUserFamilyResult = this.validateUserFamily(epId, member, isInsert ? null : familyModel.getId());
        if (validateUserFamilyResult != null) {
            return validateUserFamilyResult;
        }


        //新增家庭的校验
        if (isInsert) {
            if (this.familyDao.existsByOwnerAndEnterprise(familyModel.getOwner(), Enterprise.build(familyModel.getEpId()))) {
                return ResultContent.build(ResultState.UserExistFamily, familyModel.getOwner());
            }
        }

        //需要更新的用户缓存
        Set<String> _userCache = new HashSet<>() {{
            addAll(familyModel.getMember().stream().map((it) -> {
                return it.getUid();
            }).collect(Collectors.toSet()));
        }};


        String ret = null;
        if (isInsert) {
            ret = this.familyDao.insertFamily(familyModel);
            this.updateUserFamily(Set.of(), ret);
        } else {
            //取出原用户,需要先取消掉冗余的数据
            Set<String> oldUids = new HashSet<>();
            Optional.ofNullable(this.familyDao.findTop1ById(familyModel.getId())).ifPresent((family) -> {
                oldUids.addAll(family.getMember().stream().map((it) -> {
                    return it.getUid();
                }).collect(Collectors.toSet()));
            });
            _userCache.addAll(oldUids);

            ret = this.familyDao.modifyFamily(familyModel);
            //冗余到用户的家庭组
            this.updateUserFamily(oldUids, ret);
        }

        //通知更新用户token的缓存
        _userCache.forEach((it) -> {
            this.cacheCleanHelper.addUser(it);
        });

        return ResultContent.buildContent(ret);
    }


    /**
     * 校验家庭组成员是否有重复的
     */
    private void validateFamilyRepeatMember(Set<FamilyMember> familyMembers, String message) {
        Set<String> uids = familyMembers.stream().map((it) -> {
            return it.getUid();
        }).collect(Collectors.toSet());
        Assert.state(uids.size() == familyMembers.size(), message);
    }


    /**
     * 冗余到用户表的家庭组上
     */
    public void updateUserFamily(Set<String> cleanUids, String familyId) {
        Family family = this.familyDao.findTop1ById(familyId);
        String epId = family.getEnterprise().getId();

        //删除之前的用户冗余数据
        if (cleanUids != null && cleanUids.size() > 0) {
            this.userDao.setUserFamily(null, epId, cleanUids.toArray(new String[0]));
        }


        //增加冗余家庭组对象
        Set<String> uid = family.getMember().stream().map((it) -> {
            return it.getUid();
        }).collect(Collectors.toSet());
        //批量查询用户信息，线程安全对象
        final Map<String, BaseUserModel> userBaseMap = new ConcurrentHashMap<>(batchUserService.queryUserId(BatchQueryValue.builder().values(uid).build()));


        //构建新的家庭组信息
        com.fast.dev.auth.center.server.core.model.FamilyModel familyModel = new com.fast.dev.auth.center.server.core.model.FamilyModel();
        familyModel.setMember(family.getMember().parallelStream().map((it) -> {
            FamilyMemberModel familyMemberModel = new FamilyMemberModel();
            familyMemberModel.setUid(it.getUid());
            familyMemberModel.setIdentity(it.getIdentity());
            familyMemberModel.setNickName(it.getNickName());
            Optional.ofNullable(userBaseMap.get(it.getUid())).ifPresent((user) -> {
                familyMemberModel.setPhone(user.getPhone());
            });
            return familyMemberModel;
        }).collect(Collectors.toSet()));
        this.userDao.setUserFamily(familyModel, epId, uid.toArray(new String[0]));
    }


    @Override
    @UserLog(parameter = {"#id"})
    @Transactional
    public ResultContent<Boolean> clean(String id) {
        Assert.hasText(id, "家庭组id不能为空");

        final FamilyModel familyModel = toModel(this.familyDao.findTop1ById(id));
        if (familyModel == null) {
            return ResultContent.build(ResultState.FamilyNotExist);
        }


        //仅保留家庭的拥有者
        familyModel.setMember(familyModel.getMember().stream().filter((member) -> {
            return member.getUid().equals(familyModel.getOwner());
        }).collect(Collectors.toSet()));


        //清空家庭中的用户token的缓存
        this.cacheCleanHelper.addFamily(id);

        //更新数据库
        boolean ret = this.update(familyModel).getState() == ResultState.Success;

        if (ret) {
            //更新用户家庭组，冗余数据
            this.updateUserFamily(familyModel.getMember().stream().filter((member) -> {
                return !member.getUid().equals(familyModel.getOwner());
            }).map((it) -> {
                return it.getUid();
            }).collect(Collectors.toSet()), id);
        }

        return ResultContent.build(ret);
    }

    @UserLog
    @Override
    public ResultContent<FamilyModel> get(String id) {
        FamilyModel familyModel = toModel(this.familyDao.findTop1ById(id));
        return ResultContent.build(familyModel != null ? ResultState.Success : ResultState.FamilyNotExist, familyModel);
    }

    @Override
    public ResultContent<List<FamilyModel>> findByMemberUid(String epId, String[] uid) {
        return ResultContent.buildContent(this.familyDao.findByMember(epId, uid).stream().map((it) -> {
            return toModel(it);
        }).collect(Collectors.toList()));
    }

    @Override
    public Page<FamilyModel> list(String epId, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.familyDao.list(epId, pageable), (it) -> {
            return toModel(it);
        });
    }

    @Override
    @Transactional
    public ResultContent<Boolean> remove(String... ids) {
        for (String id : ids) {
            final Family family = this.familyDao.findTop1ById(id);
            if (family == null) {
                continue;
            }
            //清空用户token的缓存
            this.cacheCleanHelper.addFamily(id);

            //清空用户的冗余数据
            this.userDao.setUserFamily(null, family.getEnterprise().getId(), family.getMember().stream().map((it) -> {
                return it.getUid();
            }).collect(Collectors.toSet()).toArray(new String[0]));

            //数据库中删除数据
            this.familyDao.removeById(id);
        }
        return ResultContent.build(true);
    }

    /**
     * 批量校验用户id
     */
    private ResultContent<String> validateUserCenter(Set<String> uids) {
        //判断用户中心的uid合法性
        Map<String, BaseUserModel> baseUserModelMap = this.batchUserService.queryUserId(BatchQueryValue.builder().values(uids).build());
        for (String uid : uids) {
            if (!baseUserModelMap.containsKey(uid)) {
                return ResultContent.build(ResultState.UserNotExistUCenter, uid);
            }
        }
        return null;
    }


    /**
     * 校验家庭
     *
     * @param uids
     */
    private ResultContent<String> validateUserFamily(String epId, Set<String> uids, String ignoreFamilyId) {
        //取出所有的家庭
        Set<Family> families = this.familyDao.findByMember(epId, uids.toArray(new String[0])).stream().filter((family) -> {
            if (ignoreFamilyId == null) {
                return true;
            }
            return !ignoreFamilyId.equals(family.getId());
        }).collect(Collectors.toSet());
        for (Family family : families) {
            Set<String> members = family.getMember().stream().map((it) -> {
                return it.getUid();
            }).collect(Collectors.toSet());
            for (String member : members) {
                if (uids.contains(member)) {
                    return ResultContent.build(ResultState.UserExistOtherFamily, member);
                }
            }
        }
        return null;
    }


    /**
     * 转换到模型
     *
     * @param family
     * @return
     */
    private FamilyModel toModel(Family family) {
        if (family == null) {
            return null;
        }
        FamilyModel familyModel = new FamilyModel();
        BeanUtils.copyProperties(family, familyModel);
        familyModel.setEpId(family.getEnterprise().getId());
        return familyModel;
    }


}
