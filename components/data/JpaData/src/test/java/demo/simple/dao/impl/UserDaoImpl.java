package demo.simple.dao.impl;

import demo.simple.dao.extend.UserDaoExtend;
import demo.simple.domain.Group;
import demo.simple.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.UUID;

public class UserDaoImpl implements UserDaoExtend {

    @Autowired
    private EntityManager em;

    @Override
    public User findOneFromId(Long id) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        return em.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public User group() {
        User user = new User();
        user.setUserName(UUID.randomUUID().toString());
        Group group = new Group();
        group.setUser(user);
        this.em.merge(group);
        return user;
    }


}
