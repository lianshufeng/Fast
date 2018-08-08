package demo.simple.domain;


import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User extends SuperEntity {

    //用户名
    private String userName;

    //随机数
    private int r;

    //生日
    @Indexed
    private long birthdayTime;


}
