package boot;

import com.fast.dev.component.remotelock.RemoteLock;
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



    }


}
