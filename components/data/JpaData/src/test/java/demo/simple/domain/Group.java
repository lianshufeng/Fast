package demo.simple.domain;

import com.fast.dev.data.jpa.domain.SuperEntity;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "grouptable")
public class Group extends SuperEntity {

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE})
    private User user;


}
