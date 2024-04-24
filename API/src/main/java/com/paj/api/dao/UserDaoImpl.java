package org.example.api.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.api.entities.BookEntity;
import org.example.api.entities.UserEntity;

import java.util.List;
import java.util.Set;

@Stateless
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public UserEntity find(Integer id) {
        return entityManager.find(UserEntity.class, id);
    }

    @Override
    public List<UserEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        if (userEntity.getUserId() == 0) {
            entityManager.persist(userEntity);
        } else {
            entityManager.merge(userEntity);
        }
        return userEntity;
    }

    @Override
    public void delete(UserEntity userEntity) {
        entityManager.remove(entityManager.contains(userEntity) ? userEntity : entityManager.merge(userEntity));
    }
}
