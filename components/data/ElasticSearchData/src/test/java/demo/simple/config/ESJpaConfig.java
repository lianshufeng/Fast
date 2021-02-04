package demo.simple.config;

import com.fast.dev.data.jpa.es.config.ElasticsearchConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories("demo.simple.dao")
@Import(ElasticsearchConfiguration.class)
public class ESJpaConfig {
}
