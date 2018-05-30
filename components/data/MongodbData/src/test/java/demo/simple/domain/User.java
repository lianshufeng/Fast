package demo.simple.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User  {

    @Id
    private String id;

    //用户名
    private String userName;

    //随机数
    private int r;

    //时间
    @Indexed
    private long createTime;

    //生日
    @Indexed
    private long birthdayTime;



}
