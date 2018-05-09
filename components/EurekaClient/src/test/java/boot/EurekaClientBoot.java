package boot;

import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 测试代码的入口
 */

//需要使用此注解，且配置 application.properties
@EnableEurekaClient
@ComponentScan("boot")
public class EurekaClientBoot extends ApplicationBootSuper {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(EurekaClientBoot.class, args);
    }


}
