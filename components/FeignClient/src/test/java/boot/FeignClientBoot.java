package boot;

import boot.interfaces.FeignService;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Date;

/**
 * 测试代码的入口
 */

//需要使用此注解，且配置 application.properties
@EnableEurekaClient
@EnableFeignClients
@ComponentScan("boot")
@Import(MVCConfiguration.class)
public class FeignClientBoot extends ApplicationBootSuper {



    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(FeignClientBoot.class, args);



    }


}
