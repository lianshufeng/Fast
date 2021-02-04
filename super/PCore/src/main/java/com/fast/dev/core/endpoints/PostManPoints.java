package com.fast.dev.core.endpoints;

import com.fast.dev.core.conf.PostManConf;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.path.PathUtil;
import com.fast.dev.core.util.text.CodeCommentUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增加PostMan导入 api
 */
@RestController
public class PostManPoints extends SuperEndpoints {

    @Autowired
    private PostManConf postManConf;

    //bean对象探测的深度
    private final static int MaxDepthCount = 5;


    /**
     * 给postman导入接口数据
     *
     * @param request
     * @return
     */
    @RequestMapping("postman/link")
    public synchronized Object link(HttpServletRequest request) {

        //获取controller 隐射
        Set<MappingMethod> mappings = new HashSet<>();
        getMapping(request, mappings);

        //转换为postman的层级结构
        PMRoot pmRoot = new PMRoot();
        PMInfo pmInfo = new PMInfo();
        setPMName(pmInfo, postManConf.getName());
        pmRoot.setInfo(pmInfo);

        //构建父级
        PMItem pmRootItem = new PMItem();
        pmRootItem.setPath("");
        //转换为子集
        toPMItems(pmRootItem, mappings);
        pmRoot.setItem(pmRootItem.getItem());

        return pmRoot;
    }


    @Data
    public static class PMInfo extends PMSuperItem {
        private String schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PMBody {

        private String mode;

        private String raw;

        private List<Map<String, Object>> formdata;

        private Map<String, Object> options;


    }


    @Data
    public static class PMRequest {

        private String url;

        private String method;

        private List<PMValue> header;

        private PMBody body;

    }

    @Data
    public class PMValue {
        private String key;
        private String name;
        private Object value;
        private String type;
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PMItem extends PMSuperItem {

        @JsonIgnore
        private MappingMethod mappingMethod;

        @JsonIgnore
        private String path;


        private List<PMItem> item;


        private PMRequest request;


        private Object[] response = new Object[0];


        public void initParent(String path) {
            this.path = path;
            setPMName(this, path);
        }


        /**
         * 初始化Item
         *
         * @param mappingMethod
         */
        public void initNode(PMItem parentItem, MappingMethod mappingMethod) {
            final String url = mappingMethod.getUrl();
            this.path = url;
            this.mappingMethod = mappingMethod;

            //添加到父项的子项列表中
            addChildren(parentItem, this);

            //设置接口名称
            setPMName(this, url);

            //设置项上面的名称，通过读取源码
            setItemName(this, mappingMethod);


            //取出 @RequestMapping
            RequestMapping requestMapping = mappingMethod.getMethod().getAnnotation(RequestMapping.class);

            //初始化请求
            this.request = new PMRequest();

            //设置URL
            request.setUrl(postManConf.getHostName() + url);

            //设置header
            setItemHeader(this);


            //设置请求方法
            requestMethod(requestMapping, this);

            //设置请求参数
            setItemBody(mappingMethod, this);


        }


    }


    /**
     * 设置请求的header信息
     *
     * @param pmItem
     */
    private void setItemHeader(PMItem pmItem) {
        if (postManConf.getHeader() != null) {
            pmItem.getRequest().setHeader(postManConf.getHeader().entrySet().stream().map((it) -> {
                PMValue value = new PMValue();
                value.setKey(it.getKey());
                value.setName(it.getKey());
                value.setType("text");
                value.setValue(it.getValue());
                return value;
            }).collect(Collectors.toList()));
        }
    }


    /**
     * 设置项的参数部分
     */
    @SneakyThrows
    private void setItemBody(MappingMethod mappingMethod, PMItem pmItem) {
        //无参数
        final Method method = mappingMethod.getMethod();
        if (method.getParameterCount() == 0) {
            return;
        }

        //内容部分
        final PMBody body = new PMBody();
        pmItem.getRequest().setBody(body);

        //判定类型是否为json
        boolean isJson = false;
        for (Annotation[] parameterAnnotation : method.getParameterAnnotations()) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestBody) {
                    isJson = true;
                    break;
                }
            }
            if (isJson) {
                break;
            }
        }


        //json数据格式
        if (isJson) {
            //构建bean数据,深度为5
            final Object parm = BeanUtil.buildBean(mappingMethod.getMethod().getParameterTypes()[0], MaxDepthCount);
            // 构建参数的实例化的bean，支持指定深度
            body.setMode("raw");
            body.setRaw(JsonUtil.toJson(parm, true));
            body.setOptions(new HashMap<>() {{
                put("raw", new HashMap<>() {{
                    put("language", "json");
                }});
            }});
            return;
        }


        //表单数据格式
        body.setMode("formdata");
        body.setOptions(new HashMap<>() {{
            put("raw", new HashMap<>() {{
                put("language", "text");
            }});
        }});

        List<Map<String, Object>> formdata = new ArrayList<>();
        for (Map.Entry<String, BeanUtil.BeanValueInfo> entry : BeanUtil.getControllerMethodParmaInfo(method, 2).entrySet()) {
            //取出参数类型
            final BeanUtil.BeanValueInfo paramInfo = entry.getValue();
            formdata.add(new HashMap<>() {{
                put("key", entry.getKey());
                put("value", String.valueOf(paramInfo.getValue()));
                put("type", "text");
                put("description", StringUtils.hasText(paramInfo.getCommentText()) ? paramInfo.getCommentText() : "");
            }});
        }
        body.setFormdata(formdata);

    }


    /**
     * 设置请求的名称
     * 通过读取Java源码上面的注释
     */
    @SneakyThrows
    private void setItemName(PMItem pmItem, MappingMethod mappingMethod) {
        String commentText = CodeCommentUtil.readMethodComment(mappingMethod.getMethod());
        //设置项上的名称
        if (StringUtils.hasText(commentText)) {
            pmItem.setName(commentText);
        }
    }


    /**
     * 设置请求的方法
     *
     * @param pmItem
     * @param requestMapping
     */
    private void requestMethod(RequestMapping requestMapping, PMItem pmItem) {
        String defaultMethod = RequestMethod.POST.name();

        //可能有其他的注解
        if (requestMapping == null) {
            pmItem.getRequest().setMethod(defaultMethod);
            return;
        }

        //请求的方法
        RequestMethod[] requestMethods = requestMapping.method();
        if (requestMethods == null || requestMethods.length == 0) {
            pmItem.getRequest().setMethod(defaultMethod);
            return;
        }

        //如果其中有一个为post,则为post,否则为取出的第一个
        for (RequestMethod requestMethod : requestMethods) {
            if (requestMethod == RequestMethod.POST) {
                pmItem.getRequest().setMethod(defaultMethod);
                return;
            }
        }

        //设置第一个参数
        pmItem.getRequest().setMethod(requestMethods[0].name());
        return;
    }


    @Data
    public static class PMRoot {
        private PMInfo info;
        private List<PMItem> item;
    }


    @Data
    public static abstract class PMSuperItem {
        private String _postman_id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MappingMethod {

        //url地址
        private String url;

        //方法
        private Method method;

    }

    @SneakyThrows
    private void setPMName(PMSuperItem item, final String url) {
        //取出URL中的名称
        final String name = PathUtil.getName(url, "/");
        //读取配置文件的备注名
        String nickName = postManConf.getUrlMappingRemark() != null ? postManConf.getUrlMappingRemark().getOrDefault(name, name) : name;

        item.setName(nickName);
        item.set_postman_id(uuid(url));
    }


    /**
     * 转换为Uri的映射
     *
     * @param mappingMethods
     */
    private void toPMItems(PMItem pmRootItem, Set<MappingMethod> mappingMethods) {

        //临时用于存储的项
        List<PMItem> parentItemsList = new ArrayList<>();
        parentItemsList.add(pmRootItem);

        for (MappingMethod mappingMethod : mappingMethods) {
            groupPMItem(parentItemsList, mappingMethod);
        }

    }


    /**
     * 分类
     */
    private void groupPMItem(List<PMItem> parentItemsList, MappingMethod mappingMethod) {
        String parentUrl = PathUtil.getParent(mappingMethod.getUrl(), "/");

        //取出父项目
        PMItem parentItem = findParentItem(parentItemsList, parentUrl);

        //将子集增加到付项的集合里
        PMItem item = new PMItem();
        //初始化项
        item.initNode(parentItem, mappingMethod);

    }


    /**
     * 查找到父类
     *
     * @param parentUrl
     * @return
     */
    private PMItem findParentItem(List<PMItem> parentItemsList, String parentUrl) {
        for (PMItem tempItem : parentItemsList) {
            if (tempItem.getPath().equals(parentUrl)) {
                return tempItem;
            }
        }

        //没有则新创建一个
        PMItem parentItem = new PMItem();
        parentItem.initParent(parentUrl);
        parentItemsList.add(parentItem);
        parentItem.setPath(parentUrl);


        //没有查找到则新创建一个对象
        String[] uris = parentUrl.split("/");
        //0 为空字符串 , 1 为根级
        if (uris.length > 0) {
            String parentParentUrl = PathUtil.getParent(parentUrl, "/");
            PMItem parentParentItem = findParentItem(parentItemsList, parentParentUrl);
            //存在父类则加到集合中
            addChildren(parentParentItem, parentItem);
        }
        return parentItem;
    }

    /**
     * 父项中添加子项
     *
     * @param parent
     * @param child
     */
    private void addChildren(PMItem parent, PMItem child) {
        if (parent.getItem() == null) {
            parent.setItem(new ArrayList<>());
        }
        parent.getItem().add(child);
    }


    private static Map<String, PMItem> getItemMap(List<Map<String, PMItem>> tempItems, int index) {
        if (tempItems.size() <= index) {
            tempItems.add(index, new HashMap<>());
        }
        //取出所有的map
        Map<String, PMItem> itemMap = tempItems.get(index);
        if (itemMap == null) {
            itemMap = new HashMap<>();
            tempItems.add(index, itemMap);
        }
        return itemMap;
    }


    /**
     * 构建UUID
     *
     * @param name
     * @return
     */
    private static String uuid(String name) {
        return UUID.nameUUIDFromBytes(DigestUtils.md5Digest(name.getBytes())).toString();
    }


    /**
     * 获取 Mapping
     *
     * @param request
     * @param mappings
     */
    private void getMapping(HttpServletRequest request, Set<MappingMethod> mappings) {
        ServletContext servletContext = request.getSession().getServletContext();
        if (servletContext == null) {
            return;
        }
        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        //获取所有的RequestMapping
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext, HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            if (!(handlerMapping instanceof RequestMappingHandlerMapping)) {
                continue;
            }
            RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) handlerMapping;
            for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : mapping.getHandlerMethods().entrySet()) {
                RequestMappingInfo requestMappingInfo = requestMappingInfoHandlerMethodEntry.getKey();
                HandlerMethod mappingInfoValue = requestMappingInfoHandlerMethodEntry.getValue();
                PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();

                for (String pattern : patternsCondition.getPatterns()) {
                    mappings.add(MappingMethod.builder().url(pattern).method(mappingInfoValue.getMethod()).build());
                }
            }

        }


    }

}
