package demo;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



@ComponentScan("demo.simple")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoMongodbApplication extends ApplicationBootSuper {


    public static void main(String[] args){
        SpringApplication.run(DemoMongodbApplication.class, args);
    }


}
