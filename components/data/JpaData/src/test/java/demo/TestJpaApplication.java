package demo;

import com.fast.dev.core.boot.ApplicationBootSuper;
import demo.simple.config.JdbcDataConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("demo.simple")
public class TestJpaApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(TestJpaApplication.class, args);
    }
}
