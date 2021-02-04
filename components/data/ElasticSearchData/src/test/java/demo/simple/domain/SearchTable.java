package demo.simple.domain;

import com.fast.dev.data.jpa.es.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "searchtable", type = "post", indexStoreType = "fs", shards = 5, replicas = 1, refreshInterval = "-1")
public class SearchTable extends SuperEntity {

    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", index = true, type = FieldType.Text)
    private String title;


    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", index = true, type = FieldType.Text)
    private String content;

    private int userId;

    private int weight;

}