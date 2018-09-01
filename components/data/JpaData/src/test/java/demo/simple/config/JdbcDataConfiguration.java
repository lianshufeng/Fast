package demo.simple.config;

import com.fast.dev.data.jpa.config.JpaDataConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JpaDataConfiguration.class)
public class JdbcDataConfiguration {
}
