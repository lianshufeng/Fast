package com.fast.dev.component.cachemethod.aops;

import com.fast.dev.component.cachemethod.annotations.CacheMethod;
import com.fast.dev.component.cachemethod.annotations.CacheParameter;
import com.fast.dev.component.cachemethod.annotations.CleanMethod;
import com.fast.dev.component.cachemethod.models.CacheMethodModel;
import com.fast.dev.component.cachemethod.models.CleanMethodModel;
import com.fast.dev.component.cachemethod.models.ResultCacheModel;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.text.TextUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class CacheAop {

    // 缓存管理器
    private CacheManager cacheManager = CacheManager.create();
    // 缓存方法模型
    private Map<Method, CacheMethodModel> methodCacheMap = new ConcurrentHashMap<Method, CacheMethodModel>();

    // 清空缓存的方法模型
    private Map<Method, CleanMethodModel> methodCleanMap = new ConcurrentHashMap<Method, CleanMethodModel>();

    // 集合缓存字典
    private Map<String, Ehcache> collectionCache = new ConcurrentHashMap<String, Ehcache>();
    // 缓存方法寻找器
    private Map<String, Method> methodFinderCache = new ConcurrentHashMap<String, Method>();

    @Pointcut("@annotation(com.fast.dev.component.cachemethod.annotations.CacheMethod) ")
    public void cacheAop() {
    }

    @Pointcut("@annotation(com.fast.dev.component.cachemethod.annotations.CleanMethod) ")
    public void cleanAop() {
    }

    @Around("cacheAop()")
    public Object aroundCacheAop(ProceedingJoinPoint pjp) throws Throwable {
        CacheMethodModel cacheMethodModel = getMethodCacheModel(pjp);
        // 查询缓存解决方案
        ResultCacheModel resultCacheModel = readCache(pjp, cacheMethodModel);
        // 缓存没有结果，则执行原有方法，并保存到缓存里
        if (resultCacheModel.getResult() == null) {
            try {
                resultCacheModel.setResult(pjp.proceed());
            } catch (Exception e) {
                // 抛出新的异常
                throw new Exception(e);
            } finally {
                writeCache(resultCacheModel);
            }
        } else {
            log.debug("命中缓存 : " + resultCacheModel.getResult());
        }
        return resultCacheModel.getResult();
    }

    @Around("cleanAop()")
    public Object aroundCleanAop(ProceedingJoinPoint pjp) throws Throwable {
        // 取出对应的删除模型
        CleanMethodModel cleanMethodModel = getMethodCleanModel(pjp);
        removeCache(pjp, cleanMethodModel);
        return pjp.proceed();
    }

    /**
     * 是否读取缓存
     *
     * @param pjp
     * @param cacheMethodModel
     * @return
     * @throws Exception
     */
    private ResultCacheModel readCache(ProceedingJoinPoint pjp, CacheMethodModel cacheMethodModel) {
        ResultCacheModel resultCacheModel = new ResultCacheModel();
        try {
            Ehcache ehcache = getCache(cacheMethodModel);
            String index = serializationArgs(cacheMethodModel.getArgTypes(), pjp.getArgs());
            Element element = ehcache.get(index);
            Object result = element == null ? null : element.getObjectValue();
            resultCacheModel.setCache(ehcache);
            resultCacheModel.setIndex(index);
            resultCacheModel.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultCacheModel;
    }

    /**
     * 保存缓存
     *
     * @param resultCacheModel
     */
    private void writeCache(ResultCacheModel resultCacheModel) {
        Ehcache cache = resultCacheModel.getCache();
        Element element = new Element(resultCacheModel.getIndex(), resultCacheModel.getResult());
        cache.put(element);
    }

    /**
     * 删除缓存
     *
     * @param pjp
     */
    private void removeCache(ProceedingJoinPoint pjp, CleanMethodModel cleanMethodModel) {
        for (String collectionName : cleanMethodModel.getCollectionName()) {
            try {
                Ehcache ehcache = this.collectionCache.get(collectionName);
                if (ehcache != null) {
                    // String index =
                    // serializationArgs(cleanMethodModel.getArgTypes(),
                    // pjp.getArgs());
                    ehcache.removeAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 通过缓存取出方法模型
     *
     * @param pjp
     * @return
     */
    private CleanMethodModel getMethodCleanModel(ProceedingJoinPoint pjp) {
        CleanMethodModel cacheMethodModel = null;
        try {
            Method method = getProceedingJoinPointMethod(pjp);
            if (method == null) {
                return null;
            }
            cacheMethodModel = methodCleanMap.get(method);
            if (cacheMethodModel == null) {
                cacheMethodModel = new CleanMethodModel();
                // 通过注解取属性
                CleanMethod cleanMethod = method.getAnnotation(CleanMethod.class);
                cacheMethodModel.setCollectionName(cleanMethod.collectionName());
                cacheMethodModel.setMethod(method);
                // 参数注解取类型
                Boolean[] argsTypes = isArgsType(method.getParameterAnnotations());
                cacheMethodModel.setArgTypes(argsTypes);
                // #继续写哒
                methodCleanMap.put(method, cacheMethodModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheMethodModel;
    }

    /**
     * 通过缓存取出方法模型
     *
     * @param pjp
     * @return
     */
    private CacheMethodModel getMethodCacheModel(ProceedingJoinPoint pjp) {
        CacheMethodModel cacheMethodModel = null;
        try {

            Method method = getProceedingJoinPointMethod(pjp);
            if (method == null) {
                return null;
            }
            cacheMethodModel = methodCacheMap.get(method);
            if (cacheMethodModel == null) {
                cacheMethodModel = new CacheMethodModel();
                // 通过注解取属性
                CacheMethod cacheMethod = method.getAnnotation(CacheMethod.class);
                cacheMethodModel.setCollectionName(cacheMethod.collectionName());
                cacheMethodModel.setMethod(method);
                cacheMethodModel.setTimeToIdleSeconds(cacheMethod.timeToIdleSeconds());
                cacheMethodModel.setTimeToLiveSeconds(cacheMethod.timeToLiveSeconds());
                cacheMethodModel.setMaxMemoryCount(cacheMethod.maxMemoryCount());
                cacheMethodModel.setOverflowToDisk(cacheMethod.overflowToDisk());
                // 参数注解取类型
                Boolean[] argsTypes = isArgsType(method.getParameterAnnotations());
                cacheMethodModel.setArgTypes(argsTypes);
                // #继续写哒
                methodCacheMap.put(method, cacheMethodModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheMethodModel;
    }

    private Method getProceedingJoinPointMethod(ProceedingJoinPoint pjp) throws Exception {
        Signature s = pjp.getSignature();
        Class<?> cls = Class.forName(s.getDeclaringTypeName());
        String argTypesText = TextUtil.subText(s.toLongString(), "(", ")", 0).trim();
        Method method = getMethod(cls, s.getName(), argTypesText);
        return method;
    }


    /**
     * 取参数的类型， 如果参数为null，则返回 null
     *
     * @param parameters
     * @return
     */
    private static Boolean[] isArgsType(Annotation[][] parameters) {
        if (parameters == null) {
            return null;
        }
        Boolean[] isType = new Boolean[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            isType[i] = false;
            for (int j = 0; j < parameters[i].length; j++) {
                if (parameters[i][j].annotationType() == CacheParameter.class) {
                    isType[i] = true;
                    break;
                }
            }
        }
        return isType;
    }

    /**
     * 序列化参数对象
     *
     * @param args
     * @return
     * @throws Exception
     */
    private static String serializationArgs(Boolean[] argTypes, Object[] args) throws Exception {
        if (argTypes == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (argTypes[i]) {
                str.append(JsonUtil.toJson(args[i]));
            }
        }
        return String.valueOf(str);
    }

    /**
     * 获取缓存
     *
     * @return
     */
    private synchronized Ehcache getCache(CacheMethodModel cacheMethodModel) {
        String collectionName = cacheMethodModel.getCollectionName();
        Ehcache cache = this.collectionCache.get(collectionName);
        if (cache == null) {
            cache = new Cache(collectionName, cacheMethodModel.getMaxMemoryCount(), cacheMethodModel.isOverflowToDisk(),
                    false, cacheMethodModel.getTimeToLiveSeconds(), cacheMethodModel.getTimeToIdleSeconds());
            this.cacheManager.addCache(cache);
            this.collectionCache.put(collectionName, cache);
        }
        return cache;
    }

    /**
     * 通过参数反射寻找对应的方法
     *
     * @param cls
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    private Method getMethod(Class<?> cls, String methodName, String classeNameTexts)
            throws NoSuchMethodException, SecurityException, ClassNotFoundException {
        String index = cls.getName() + "." + methodName + "(" + classeNameTexts + ")";
        Method method = this.methodFinderCache.get(index);
        if (method == null) {
            List<Class<?>> list = new ArrayList<Class<?>>();
            String[] classesNames = classeNameTexts.split(",");
            for (String className : classesNames) {
                if (!StringUtils.isEmpty(className)) {
                    list.add(Class.forName(className));
                }
            }
            Class<?>[] os = list.toArray(new Class<?>[list.size()]);
            method = cls.getMethod(methodName, os);
            method.setAccessible(true);
            this.methodFinderCache.put(index, method);
        }
        return method;
    }

    @PreDestroy
    private void shutdown() {
        cacheManager.shutdown();
    }

}