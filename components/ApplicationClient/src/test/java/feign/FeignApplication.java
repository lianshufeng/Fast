package feign;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 测试代码的入口
 */

//需要使用此注解，且配置 application.properties

@EnableApplicationClient
@ComponentScan("feign")
@Import(MVCConfiguration.class)
public class FeignApplication extends ApplicationBootSuper {



    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(FeignApplication.class, args);



    }


}
