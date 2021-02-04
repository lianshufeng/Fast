package com.fast.dev.acenter.feign;

import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.data.web.PageableDefault;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class FeignPageableDefaultParameterProcessor implements AnnotatedParameterProcessor {


    private static final Class<PageableDefault> ANNOTATION = PageableDefault.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterProcessor.AnnotatedParameterContext context,
                                   Annotation annotation, Method method) {
        int paramIndex = context.getParameterIndex();
        MethodMetadata metadata = context.getMethodMetadata();
        if (metadata.queryMapIndex() == null) {
            metadata.queryMapIndex(paramIndex);
        }
        return true;
    }

}
