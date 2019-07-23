package com.fast.dev.components.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfig extends ApiInfoBuilder {

    private String title;
    private String description;
    private String termsOfServiceUrl;
    private Contact contact;
    private String license;
    private String licenseUrl;
    private String version;
    private List<VendorExtension> vendorExtensions = newArrayList();


    /**
     * 需要扫描的包
     */
    private String packageName;


    @Override
    public ApiInfo build() {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Method method = BeanUtils.findMethod(this.getClass(), field.getName(), field.getType());
                if (method!=null){
                    method.invoke(this, field.get(this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.build();
    }
}
