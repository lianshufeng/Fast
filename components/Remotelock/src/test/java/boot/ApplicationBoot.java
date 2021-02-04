package boot;

import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 测试代码的入口
 */
@ComponentScan("com.fast.dev.component.remotelock")
@ComponentScan("boot")
public class ApplicationBoot extends ApplicationBootSuper {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(ApplicationBoot.class, args);


        try {
            ac.getBean(Demo_Queue.class).run();
//            ac.getBean(Demo_Get.class).run();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
