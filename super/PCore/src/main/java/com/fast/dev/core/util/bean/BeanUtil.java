package com.fast.dev.core.util.bean;

import com.fast.dev.core.util.text.CodeCommentUtil;
import com.fast.dev.core.util.text.TextUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * Bean工具
 */
public class BeanUtil {

    //默认的基础类型的实例化初始值
    private static final Map<Class, Object> BaseType = new HashMap<>() {
        {
            put(int.class, 0);
            put(long.class, 0l);
            put(short.class, 0);
            put(byte.class, 0);
            put(float.class, 0f);
            put(double.class, 0d);
            put(boolean.class, false);
            put(char.class, "");

            put(Integer.class, 0);
            put(Long.class, 0l);
            put(Short.class, 0);
            put(Byte.class, 0);
            put(Float.class, 0f);
            put(Double.class, 0d);
            put(Boolean.class, false);

            put(BigDecimal.class, 0);
        }
    };

    // 参数名发现
    private final static LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * javaBean转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> bean2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        try {
            // 获取javaBean的BeanInfo对象
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            // 获取属性描述器
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                // 获取属性名
                String key = propertyDescriptor.getName();
                // 获取该属性的值
                Method readMethod = propertyDescriptor.getReadMethod();
                // 通过反射来调用javaBean定义的getName()方法
                Object value = readMethod.invoke(obj);
                map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 读取一个bean的类型
     *
     * @return
     */
    @SneakyThrows
    public static Map<String, Class> readBeanType(Class<?> cls) {
        Map<String, Class> ret = new HashMap<>();
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            ret.put(propertyDescriptor.getName(), propertyDescriptor.getPropertyType());
        }
        return ret;
    }


    /**
     * 通过map设置bean对象
     *
     * @param obj
     * @param map
     */
    @SneakyThrows
    public static void setBean(final Object obj, final Map<String, Object> map) {
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取属性名
            String key = propertyDescriptor.getName();
            if (!map.containsKey(key)) {
                continue;
            }


            // 兼容 @Accessors(chain = true)
            // 获取该属性的值
            Method writeMethod = propertyDescriptor.getWriteMethod();

            //如果为空则通过反射取出来
            if (writeMethod == null) {
                writeMethod = obj.getClass().getMethod(toMethodName(propertyDescriptor.getName()), propertyDescriptor.getPropertyType());
            }

            // 通过反射来调用javaBean定义的getName()方法
            if (writeMethod == null) {
                continue;
            }

            //调用方法
            final Class propertyType = propertyDescriptor.getPropertyType();
            Object sourceObj = map.get(key);
            if (sourceObj == null) {
                continue;
            }

            //递归类型 ( 当value 为 map)
            if (propertyType != sourceObj.getClass() && sourceObj instanceof Map) {
                Object ret = newClass(propertyType);
                setBean(ret, (Map) sourceObj);
                writeMethod.invoke(obj, ret);
            } else if (sourceObj instanceof Collection || sourceObj.getClass().isArray()) {
                writeMethod.invoke(obj, toTypeFromCollectionObject(propertyType, sourceObj));
            } else {
                writeMethod.invoke(obj, sourceObj);
            }
        }
    }

    /**
     * 转换到目标类型
     *
     * @param <T>
     * @return
     */
    private static <T> T toTypeFromCollectionObject(Class<T> targetCls, Object sourceObj) {

        //处理集合类型
        List items = null;
        if (sourceObj instanceof Collection) {
            items = (List) ((Collection) sourceObj).stream().collect(Collectors.toList());
        } else if (targetCls.isArray()) {
            items = Arrays.stream((Object[]) sourceObj).collect(Collectors.toList());
        } else {
            items = new ArrayList();
        }


        //目标类型
        if (targetCls.isArray()) {
            Object ret = Array.newInstance(targetCls.getComponentType(), items.size());
            for (int i = 0; i < items.size(); i++) {
                Array.set(ret, i, items.get(i));
            }
            return (T) ret;
        }


        if (targetCls == List.class) {
            return (T) new ArrayList(items);
        }
        if (targetCls == Set.class) {
            return (T) new HashSet<>(items);
        }
        if (targetCls == Vector.class) {
            return (T) new Vector<>(items);
        }
//        sourceObj instanceof Map sourceObj instanceof Collections || sourceObj.getClass().isArray()

        return null;
    }

    /**
     * 转换到方法名
     *
     * @param name
     * @return
     */
    private static String toMethodName(String name) {
        return "set" + name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    /**
     * Bean转到Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> m = new HashMap<>();
        setMap(m, obj);
        return m;
    }


    @SneakyThrows
    public static <T> T getBeanValue(Object obj, String name) {
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取属性名
            String key = propertyDescriptor.getName();
            if (key.equals(name)) {
                Method readMethod = propertyDescriptor.getReadMethod();
                return (T) readMethod.invoke(obj, name);
            }
        }
        return null;
    }


    /**
     * bean转到Map
     *
     * @param map
     * @param obj
     */
    @SneakyThrows
    public static void setMap(final Map<String, Object> map, final Object obj) {
        //如果对象为Map，直接拷贝到目标Map中
        if (obj instanceof Map) {
            map.putAll((Map) obj);
            return;
        }
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取属性名
            String key = propertyDescriptor.getName();
            Method readMethod = propertyDescriptor.getReadMethod();
            map.put(key, readMethod.invoke(obj));
        }
    }


    /**
     * 获取bean对象中为null的属性名
     *
     * @param source
     * @return
     */
    public static void getNullPropertyNames(Object source, Set<String> sets) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) sets.add(pd.getName());
        }
    }


    /**
     * 构建Bean对象
     *
     * @param cls
     * @param componentType
     * @param <T>
     * @return
     */
    @SneakyThrows
    private static <T> T buildBean(final Class<T> cls, final Class componentType, int currentDepth,
                                   int maxDepthCount) {
        //集合对象
        Object items = null;
        //单独的属性
        Object item = null;
        //处理数组与集合
        if (cls.isArray()) {
            items = Array.newInstance(componentType, 1);
            item = newClass(componentType);
            Array.set(items, 0, item);
        } else if (Collection.class.isAssignableFrom(cls)) {
            item = newClass(componentType);
            if (cls == Collection.class) {
                items = List.of(item);
            } else {
                //取出ofMethod 的方法
                Method ofMethod = cls.getDeclaredMethod("of", Object.class);
                if (ofMethod != null) {
                    ofMethod.setAccessible(true);
                    items = ofMethod.invoke(null, item);
                }
            }
        } else if (Map.class.isAssignableFrom(cls)) {
            items = new HashMap<>();
        } else {
            item = newClass(cls);
        }

        //深度执行 , 过滤基础类型与字符串类型
        if (item != null && !(item instanceof String) && currentDepth < maxDepthCount && item.getClass() != Object.class && !item.getClass().isEnum()) {
            depthSetBean(item, ++currentDepth, maxDepthCount);
        }
        //集合对象不为空则返回集合对象，否则返回单个的对象
        return (T) (items != null ? items : item);
    }


    /**
     * 获取方法上的参数名
     *
     * @param method
     * @return
     */
    public static String[] getParameterNames(Method method) {
        return parameterNameDiscoverer.getParameterNames(method);
    }

    /**
     * 取方法名上的参数名，优先遵循是 RequestParam 注解
     *
     * @param method
     * @param index
     * @return
     */
    public static String getMethodParamName(Method method, final int index) {
        String[] paramNames = getParameterNames(method);
        //读取参数名,优先读取注解上的参数名
        String paramName = null;
        Annotation[] parameterAnnotations = method.getParameterAnnotations()[index];
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) parameterAnnotation;
                return requestParam.value();
            }
        }
        if (paramName == null) {
            paramName = paramNames[index];
        }

        return paramName;
    }


    /**
     * 取出controller的方法的所有属性以及类型
     *
     * @param method
     * @return
     */
    public static Map<String, BeanValueInfo> getControllerMethodParmaInfo(final Method method,
                                                                          final int maxDepthCount) {
        Map<String, String> fieldComment = new HashMap<>();
        CodeCommentUtil.readMethodComment(method, fieldComment);
        //当前深度
        Map<String, BeanValueInfo> ret = new HashMap<>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            String paramName = getMethodParamName(method, i);
            Class paramType = method.getParameterTypes()[i];

            //深度设置参数值
            setDepthParmaInfo(ret, null, paramName, paramType, fieldComment.get(paramName), 0, maxDepthCount);
        }
        return ret;
    }


    /**
     * 深度设置参数或bean的值
     *
     * @param ret
     * @param spaceName
     * @param paramName
     * @param paramType
     * @param currentDepthCount
     * @param maxDepthCount
     */
    @SneakyThrows
    private static void setDepthParmaInfo(final Map<String, BeanValueInfo> ret, final String spaceName,
                                          final String paramName, final Class paramType, final String fieldCommentText, int currentDepthCount,
                                          int maxDepthCount) {
        //参数类型的名称
        String paramTypeName = paramType.getName();

        if (paramType == Class.class) {
            //过滤不处理
        } else if (BaseType.containsKey(paramType)) {
            //基础类型
            setBeanMapItem(ret, spaceName, paramName, fieldCommentText, BaseType.get(paramType));
        } else if (paramType == String.class) {
            //字符串
            setBeanMapItem(ret, spaceName, paramName, fieldCommentText, new String());
        } else if (paramType.isEnum()) {
            //枚举类
            String value = TextUtil.join(Arrays.stream(paramType.getFields()).map((it) -> {
                it.setAccessible(true);
                return it.getName();
            }).collect(Collectors.toList()).toArray(new String[0]), ",");
            setBeanMapItem(ret, spaceName, paramName, fieldCommentText, value);
        } else if (paramTypeName.equals("org.springframework.data.domain.Pageable")) {
            //分页模型
            setBeanMapItem(ret, spaceName, "page", "分页查询: 当前页码,第一页为0", 0);
            setBeanMapItem(ret, spaceName, "size", "分页查询: 每页显示数量", 10);
            setBeanMapItem(ret, spaceName, "sort", "分页查询: 排序", "createTime,desc");
        } else if (paramType.isArray() || Collection.class.isAssignableFrom(paramType)) {
            //数组或集合
            setBeanMapItem(ret, spaceName, paramName, fieldCommentText, "test1,test2");
        } else if (Map.class.isAssignableFrom(paramType)) {
            //处理Map
            setBeanMapItem(ret, spaceName, paramName + "['key']", fieldCommentText, "test");
        } else if (paramType.isInterface() || Modifier.isAbstract(paramType.getModifiers())) {
            //不处理接口或抽象类
        } else if (currentDepthCount < maxDepthCount) {
            //自定义类型的处理,必须保证有至少一个默认的构造方法
            int depthCount = ++currentDepthCount;
            //当前对象的命名空间
            final String objSpaceName = StringUtils.hasText(spaceName) ? spaceName + "." + paramName : paramName;
            try {
                //遍历所有属性
                final BeanWrapper src = new BeanWrapperImpl(paramType);
                java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
                for (java.beans.PropertyDescriptor pd : pds) {
                    Class beanType = pd.getPropertyType();
                    if (beanType == Class.class) {
                        continue;
                    }
                    String beanName = pd.getName();
                    //深度识别类型
                    setDepthParmaInfo(ret, objSpaceName, beanName, beanType, getFieldCommentText(paramType, beanName), depthCount, maxDepthCount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 设置BeanMap的项
     *
     * @param ret
     * @param spaceName
     * @param itemKey
     * @param fieldCommentText
     * @param value
     */
    private static void setBeanMapItem(final Map<String, BeanValueInfo> ret, final String spaceName,
                                       final String itemKey, String fieldCommentText, Object value) {
        String key = getItemKeyName(spaceName, itemKey);
        if (StringUtils.hasText(key)) {
            ret.put(key, new BeanValueInfo(key, fieldCommentText, value));
        }
    }

    /**
     * 获取项目的key
     *
     * @param spaceName
     * @param itemKey
     * @return
     */
    private static String getItemKeyName(String spaceName, String itemKey) {
        String ret = StringUtils.hasText(spaceName) && StringUtils.hasText(itemKey) ? spaceName + "." + itemKey : itemKey;
        if (ret.indexOf(".") < 0) {
            return ret;
        }
        //根据spring controller的规则，如果有多级对象，则去掉首个
        String[] items = ret.split("\\.");
        if (items.length > 1) {
            String[] newItems = new String[items.length - 1];
            System.arraycopy(items, 1, newItems, 0, newItems.length);
            ret = TextUtil.join(newItems, ".");
        }
        return ret;
    }


    /**
     * 参数信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeanValueInfo {

        //名称
        private String name;

        //类型
        private String commentText;

        //值
        private Object value;

//        //方法
//        private String commentText;
//
//        //参数注释
//        private Map<String,String> paramCommentText;

    }


    /**
     * 构建bean，支持深度遍历构建
     *
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T buildBean(Class<T> cls, int maxDepthCount) {
        return buildBean(cls, cls.getComponentType(), 0, maxDepthCount);
    }


    /**
     * 通过属性构建bean对象
     *
     * @param field
     * @param <T>
     * @return
     */
    private static <T> T buildBean(Field field, int currentDepth, int maxDepthCount) {
        Class type = field.getType();
        Class[] cls = getGenericType(field);
        Object ret = buildBean(type, (cls != null && cls.length > 0) ? cls[0] : type.getComponentType(), currentDepth, maxDepthCount);
        return ret == null ? null : (T) ret;
    }


    /**
     * 实例化对象
     *
     * @param cls
     */
    @SneakyThrows
    public static <T> T newClass(Class<T> cls) {
        //基础类型,则直接返回null
        Object val = BaseType.get(cls);
        if (val != null) {
            return (T) val;
        }
        //枚举类
        if (cls.isEnum()) {
            return (T) cls.getFields()[0].get(null);
        }

        // 数组与集合
        if (cls.isArray()) {
            return (T) Array.newInstance(cls, 0);
        } else if (cls == List.class) {
            return (T) new ArrayList();
        } else if (cls == Set.class) {
            return (T) new HashSet<>();
        } else if (cls == Map.class) {
            return (T) new HashMap<>();
        }


        Constructor<?> newConstructor = null;
        //遍历出所有的构造方法
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                newConstructor = constructor;
                break;
            }
        }
        Assert.notNull(newConstructor, "未找到可用的构造方法 : " + cls);
        return (T) newConstructor.newInstance();
    }


    /**
     * 获取枚举类的class
     *
     * @param field
     * @return
     */
    public static Class[] getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (null == genericType) {
            return null;
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            // 得到泛型里的class类型对象
            return Arrays.stream(pt.getActualTypeArguments()).map((it) -> {
                return (Class) it;
            }).collect(Collectors.toList()).toArray(new Class[0]);
        }
        return null;
    }

    /**
     * 深度设置Bean的默认值,调用Set方法进行赋值
     *
     * @param item
     */
    @SneakyThrows
    private static <T> void depthSetBean(Object item, int currentDepth, int maxDepthCount) {
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(item.getClass(), Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            //属性
            Field field = getField(item.getClass(), name);
            if (field == null) {
                continue;
            }
            // 根据类型进行构建Bean
            Object val = buildBean(field, currentDepth, maxDepthCount);
            if (val != null) {
                // 获取该属性的值
                Method readMethod = propertyDescriptor.getWriteMethod();
                // 通过反射来调用javaBean定义的getName()方法
                readMethod.invoke(item, val);
            }
        }
    }


    /**
     * 获取class的属性
     *
     * @param cls
     * @param fieldName
     * @return
     */
    @SneakyThrows
    private static Field getField(Class cls, String fieldName) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }
        Field field = null;
        if (cls.getSuperclass() != null) {
            field = getField(cls.getSuperclass(), fieldName);
        }
        return field;
    }

    /**
     * 读取属性上面的注释
     *
     * @param cls
     * @param fieldName
     * @return
     */
    @SneakyThrows
    private static String getFieldCommentText(Class cls, String fieldName) {
        return CodeCommentUtil.readFieldComment(getField(cls, fieldName));
    }


}
