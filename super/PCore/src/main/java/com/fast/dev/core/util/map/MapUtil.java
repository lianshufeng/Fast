package com.fast.dev.core.util.map;

import com.fast.dev.core.util.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtil {


    /**
     * 分割Map对象
     *
     * @return
     */
    public static <K, V> Map<K, V>[] split(Map<K, V> map, Class<? extends Map> cls, int chunkNum) {
        List<Map<K, V>> lists = new ArrayList<>() {{
            for (int i = 0; i < chunkNum; i++) {
                add(BeanUtil.newClass(cls));
            }
        }};
        Map<K, V>[] ret = lists.toArray(new Map[0]);

        long i = 0;
        //开始分割
        for (Map.Entry<K, V> entry : map.entrySet()) {
            int index = (int) (i % chunkNum);
            ret[index].put(entry.getKey(), entry.getValue());
            i++;
        }

        return ret;
    }


}
