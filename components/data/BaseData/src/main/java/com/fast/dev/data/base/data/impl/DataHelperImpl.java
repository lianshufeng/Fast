package com.fast.dev.data.base.data.impl;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.base.data.DataHelper;
import com.fast.dev.data.base.data.annotations.DataRule;
import com.fast.dev.data.base.data.annotations.DataUpdate;
import com.fast.dev.data.base.data.model.UpdateDataDetails;
import lombok.extern.java.Log;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据助手
 */
@Log
@Deprecated
public abstract class DataHelperImpl implements DataHelper {


    /**
     * 获取注解
     *
     * @param entityClasses
     * @return
     */
    protected Map<Field, DataRule[]> getAnnotations(Class<? extends AbstractPersistable> entityClasses) {
        Map<Field, DataRule[]> ret = new HashMap<>();
        listAnnotations(ret, entityClasses);
        return ret;
    }


    /**
     * 获取原数据
     *
     * @return
     */
    public abstract Object getSource(Class<? extends AbstractPersistable> entityClasses, Object id);


    /**
     * 获取目标数据的规则
     *
     * @param dataRule
     * @return
     */
    public abstract String[] getTargerDataRuleIds(DataRule dataRule, Map<String, Object> varMap);


    /**
     * 获取所有注解
     *
     * @param ret
     * @param entityClasses
     */
    private void listAnnotations(Map<Field, DataRule[]> ret, Class<? extends AbstractPersistable> entityClasses) {
        for (Field field : entityClasses.getDeclaredFields()) {
            field.setAccessible(true);
            DataUpdate dataUpdate = field.getAnnotation(DataUpdate.class);
            if (dataUpdate != null) {
                ret.put(field, dataUpdate.value());
            }
        }
        if (!entityClasses.getSuperclass().equals(AbstractPersistable.class)) {
            listAnnotations(ret, (Class<? extends AbstractPersistable>) entityClasses.getSuperclass());
        }
    }


    /**
     * 更新数据源
     *
     * @param fieldObject
     * @param targetEntityIds
     * @param dataRule
     */
    public abstract void updateData(Object fieldObject, String[] targetEntityIds, DataRule dataRule);


    @Override
    public UpdateDataDetails[] update(Class<? extends AbstractPersistable> entityClasses, Object id) {
        List<UpdateDataDetails> updateDataDetails = new ArrayList<>();
        if (id == null) {
            return null;
        }

        //获取原数据
        Object source = getSource(entityClasses, id);
        if (source == null) {
            return null;
        }

        //表达式变量
        Map<String, Object> varMap = BeanUtil.bean2Map(source);

        //可能需要同步的数据
        for (Map.Entry<Field, DataRule[]> entry : getAnnotations(entityClasses).entrySet()) {
            try {
                //实体对应的数据
                Object fieldObject = entry.getKey().get(source);
                //更新数据规则
                for (DataRule dataRule : entry.getValue()) {
                    //获取需要更新的id列表
                    String[] targetEntityIds = getTargerDataRuleIds(dataRule, varMap);
                    //更新数据
                    updateData(fieldObject, targetEntityIds, dataRule);

                    //将结果更新到集合里
                    updateDataDetails.add(UpdateDataDetails.builder().ids(targetEntityIds).entity(dataRule.targetEntity()).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return updateDataDetails.toArray(new UpdateDataDetails[updateDataDetails.size()]);
    }


}
