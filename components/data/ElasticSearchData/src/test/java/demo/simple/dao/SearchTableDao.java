package demo.simple.dao;

import com.fast.dev.data.jpa.es.dao.ElasticSearchDao;
import demo.simple.dao.extend.SearchTableDaoExtend;
import demo.simple.domain.SearchTable;
import org.springframework.data.elasticsearch.annotations.Query;

import java.util.List;


public interface SearchTableDao extends ElasticSearchDao<SearchTable>, SearchTableDaoExtend {

    List<SearchTable> findByTitleIs(String title);


    long countByTitleIs(String title);




    List<SearchTable> findByTitleLike(String title);



}
