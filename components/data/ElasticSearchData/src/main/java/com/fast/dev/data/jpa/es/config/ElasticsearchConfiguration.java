package com.fast.dev.data.jpa.es.config;

import com.fast.dev.data.jpa.es.custom.CustomElasticsearchTemplate;
import com.fast.dev.data.jpa.es.custom.CustomResultMapper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.util.HotSwapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mapping.context.MappingContext;

@Configuration
public class ElasticsearchConfiguration {

    @Autowired
    private MappingContext mappingContext;

    @Autowired
    private Client client;

    @Bean
    public CustomResultMapper customResultMapper() {
        return new CustomResultMapper(mappingContext);
    }


    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() throws Exception {
        return new CustomElasticsearchTemplate(client, customResultMapper());
    }


//    @Autowired
    private void updateCode_doCount1() throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(ElasticsearchTemplate.class.getName());
        CtMethod m = cc.getDeclaredMethod("doCount", new CtClass[]{cp.get(SearchRequestBuilder.class.getName()), cp.get(QueryBuilder.class.getName())});

//        m.setBody("if (elasticsearchQuery != null) {\n" +
//                "\t\t\tcountRequestBuilder.setQuery(elasticsearchQuery);\n" +
//                "\t\t}\n" +
//                "\t\treturn countRequestBuilder.execute().actionGet().getHits().getTotalHits().value;");

        m.setBody("return 0 ;");
        cc.writeFile();

//        HotSwapper swap = new HotSwapper(8000);
//        swap.reload(ElasticsearchTemplate.class.getName(), cc.toBytecode());


    }

//    @Autowired
    private void updateCode_doCount2() throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(ElasticsearchTemplate.class.getName());
        CtMethod m = cc.getDeclaredMethod("doCount", new CtClass[]{cp.get(SearchRequestBuilder.class.getName()), cp.get(QueryBuilder.class.getName()), cp.get(QueryBuilder.class.getName())});

//        m.setBody("if (elasticsearchQuery != null) {\n" +
//                "\t\t\tcountRequestBuilder.setQuery(elasticsearchQuery);\n" +
//                "\t\t}\n" +
//                "\t\treturn countRequestBuilder.execute().actionGet().getHits().getTotalHits().value;");

        m.setBody("return 0 ;");
        cc.writeFile();

//        HotSwapper swap = new HotSwapper(8000);
//        swap.reload(ElasticsearchTemplate.class.getName(), cc.toBytecode());


    }

}
