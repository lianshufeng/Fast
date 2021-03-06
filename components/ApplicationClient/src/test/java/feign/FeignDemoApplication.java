package feign;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 测试代码的入口
 */

//需要使用此注解，且配置 application.properties


@ComponentScan("feign")
@EnableFeignClients //必要
@EnableApplicationClient
public class FeignDemoApplication extends ApplicationBootSuper {



    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(FeignDemoApplication.class, args);



    }


}
