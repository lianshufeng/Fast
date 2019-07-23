package demo.simple.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class UserInfo extends SuperEntity {


    private String uid;

    /**
     * 同步用户表的username
     */
    private String name;

    @Indexed
    private String other;


}
