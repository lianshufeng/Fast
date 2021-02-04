package demo.simple.config;

import com.fast.dev.data.token.config.ResourceTokenConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ResourceTokenConfiguration.class)
public class ResourceTokenConfig {

}
