package com.fast.dev.acenter.feign;

import feign.codec.EncodeException;
import feign.querymap.FieldQueryMapEncoder;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

public class FeignPageableDefaultFieldQueryMapEncoder extends FieldQueryMapEncoder {


    @Override
    public Map<String, Object> encode(Object object) throws EncodeException {
        LinkedMultiValueMap<String, Object> ret = new LinkedMultiValueMap();
        super.encode(object).entrySet().forEach((entry) -> {
            if (!entry.getKey().equals("sort")) {
                ret.add(entry.getKey(), entry.getValue());
            }
        });

        //特殊处理pageable
        if (object instanceof Pageable) {
            process(ret, (Pageable) object);
        }

        return (Map) ret;
    }

    /**
     * 重写sort
     *
     * @param ret
     * @param pageable
     */
    private void process(LinkedMultiValueMap<String, Object> ret, Pageable pageable) {
        pageable.getSort().forEach((sort) -> {
            ret.add("sort", sort.getProperty() + "," + sort.getDirection().name());
        });
    }


}
