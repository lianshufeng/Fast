package demo;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import demo.simple.dao.SearchTableDao;
import demo.simple.domain.SearchTable;
import org.elasticsearch.action.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;

import java.net.InetAddress;
import java.net.UnknownHostException;


@ComponentScan("demo.simple")
public class DemoElasticSearchDataApplication extends ApplicationBootSuper {


    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(DemoElasticSearchDataApplication.class, args);


//        ElasticsearchTemplate elasticsearchTemplate = applicationContext.getBean(ElasticsearchTemplate.class);
//        String documentId = "123456";
//        SearchTable searchTable = new SearchTable();
//        searchTable.setId(documentId);
//        searchTable.setContent("content");
//        searchTable.setTitle("title");
//        IndexQuery indexQuery = new IndexQueryBuilder().withId(searchTable.getId()).withObject(searchTable).build();
//        elasticsearchTemplate.index(indexQuery);
//
//        System.out.println("---");

        // jpa 插入数据
        SearchTable searchTable = new SearchTable();
        searchTable.setTitle("_title" + System.currentTimeMillis());
        searchTable.setContent("_conten中文测试");

        SearchTableDao searchTableDao = applicationContext.getBean(SearchTableDao.class);
        searchTableDao.save(searchTable);

        ElasticsearchTemplate elasticsearchTemplate = applicationContext.getBean(ElasticsearchTemplate.class);
//
//
        CriteriaQuery criteriaQuery = new CriteriaQuery(Criteria.where("title").is("_title"));
        System.out.println(elasticsearchTemplate.queryForList(criteriaQuery, SearchTable.class));
//

        System.out.println(elasticsearchTemplate.count(criteriaQuery));
//
        System.out.println(searchTableDao.countByTitleIs("_title"));


    }


}
