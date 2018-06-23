package demo;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@ComponentScan("demo.simple")
public class DemoMongodbApplication extends ApplicationBootSuper {


    public static void main(String[] args){
        SpringApplication.run(DemoMongodbApplication.class, args);
    }


}
