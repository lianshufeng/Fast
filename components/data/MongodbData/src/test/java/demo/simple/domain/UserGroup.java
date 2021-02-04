package demo.simple.domain;

import com.fast.dev.data.base.data.annotations.sync.DataSync;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户组
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class UserGroup extends SuperEntity {


    @DBRef(lazy = true)
    private User master;


    //管理员名称
    @Indexed
    private String masterName;


    @Indexed
    //测试故意形成环
    @DataSync(targetEntity = User.class, targetField = "userGroupRevName", targetQuery = "{'_id': ObjectId('$master.id')}")
    private String firstName;

}
