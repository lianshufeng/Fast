package demo.simple.domain;

import com.fast.dev.data.jpa.es.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "searchtable", type = "post", indexStoreType = "fs", shards = 5, replicas = 1, refreshInterval = "-1")
public class SearchTable extends SuperEntity {

    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;


    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    private int userId;

    private int weight;

    @Override
    public String toString() {
        return "SearchTable{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", weight=" + weight +
                '}';
    }

}