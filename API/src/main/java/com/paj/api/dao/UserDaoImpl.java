package com.paj.api.dao;

import com.paj.api.entities.UserEntity;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserEntity find(Integer id) {
        return entityManager.find(UserEntity.class, id);
    }

    public UserEntity findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getSingleResult();
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
