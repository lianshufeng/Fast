package demo.simple.domain;


import com.fast.dev.data.base.data.annotations.sync.DataSync;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@NoArgsConstructor
@ToString(callSuper = true)
public class User extends SuperEntity {

    //用户名
//    @DataUpdate({
//            @DataRule(targetEntity = UserGroup.class, targetField = "masterName", targetQuery = "{ 'master' : { '\\$ref':'user' , '\\$id': ObjectId('$id') } }"),
//            @DataRule(targetEntity = UserInfo.class, targetField = "name", targetQuery = "{'uid': '$id' }")
//    })

    @DataSync(targetEntity = UserGroup.class, targetField = "masterName", targetQuery = "{ 'master' : { '\\$ref':'user' , '\\$id': ObjectId('$id') } }")
    @DataSync(targetEntity = UserInfo.class, targetField = "name", targetQuery = "{'uid': '$id' }")
//    @DataSync(targetEntity = UserInfo.class, targetField = "name", targetQuery = "{'uid': '$id' }", targetExpression = "userName.substring(0,userName.length() - 1)")
    private String userName;

    //随机数
    @Indexed
    private int r;

    //生日
    @Indexed
    private long birthdayTime;


    //接收到的数据
    @Indexed
    private String userGroupRevName;


    public User(String userName) {
        this.userName = userName;
    }
}
