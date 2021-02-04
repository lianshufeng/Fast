package demo.simple.domain;

import com.fast.dev.data.jpa.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * 数据库实体
 */
@Data
@Entity
public class User extends  SuperEntity  {

    @Column(unique = true, nullable = true)
    private String userName;

    @Column(unique = false, nullable = true)
    private int age;

    @Column(unique = false, nullable = true)
    private boolean sex;

}
