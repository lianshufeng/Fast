package com.fast.dev.ucenter.security.resauth;

import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuths;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ResourceAuthSecuredAnnotationSecurityMetadataSource extends SecuredAnnotationSecurityMetadataSource {


    private Class<? extends Annotation> annotationType;


    private Set<Class<? extends Annotation>> annotationSet = new HashSet<>() {{
        add(ResourceAuth.class);
        add(ResourceAuths.class);
    }};


    @Autowired
    private ResourceAuthAnnotationMetadataExtractor resourceAuthAnnotationMetadataExtractor;

    public ResourceAuthSecuredAnnotationSecurityMetadataSource() {
        super();
    }

    @SneakyThrows
    public ResourceAuthSecuredAnnotationSecurityMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);

        //取注解
        Field annotationTypeField = this.getClass().getSuperclass().getDeclaredField("annotationType");
        annotationTypeField.setAccessible(true);
        annotationType = (Class<? extends Annotation>) annotationTypeField.get(this);


    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return processAnnotation(AnnotationUtils.findAnnotation(clazz, annotationType));
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        if (annotationType != ResourceAuths.class) {
            return processAnnotation(AnnotationUtils.findAnnotation(method, annotationType));
        }
        return processAnnotation(scanResourceAuths(method));
    }


    /**
     * 自定义扫描器
     *
     * @param a
     * @return
     */
    private Collection<ConfigAttribute> processAnnotation(Annotation a) {
        if (a == null) {
            return null;
        }

        Collection<ConfigAttribute> ret = new ArrayList<>();
        if (a instanceof ResourceAuths) {
            processAnnotation(ret, resourceAuthAnnotationMetadataExtractor.extractAttributes((ResourceAuths) a));
        } else if (a instanceof ResourceAuth) {
            processAnnotation(ret, resourceAuthAnnotationMetadataExtractor.extractAttributes((ResourceAuth) a));
        }
        return ret.size() == 0 ? null : ret;
    }

    private void processAnnotation(Collection<ConfigAttribute> ret, Collection<ConfigAttribute> configAttributes) {
        if (configAttributes != null) {
            ret.addAll(configAttributes);
        }
    }


    /**
     * 扫描当前方法上是否有需要的权限注解
     *
     * @param method
     * @return
     */
    private Annotation scanResourceAuths(Method method) {
        for (Class<? extends Annotation> it : annotationSet) {
            Annotation resAuthsAnnotation = AnnotationUtils.findAnnotation(method, it);
            if (resAuthsAnnotation != null) {
                return resAuthsAnnotation;
            }
        }
        return null;
    }


}
